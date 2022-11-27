package net.catena_x.btp.hi.oem.backend.hi_service.collector;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.catena_x.btp.hi.oem.backend.hi_service.collector.util.*;
import net.catena_x.btp.hi.oem.backend.hi_service.notifications.dao.supplierhiservice.HINotificationToSupplierContentDAO;
import net.catena_x.btp.hi.oem.backend.hi_service.notifications.dto.supplierhiservice.DataToSupplierContent;
import net.catena_x.btp.hi.oem.backend.hi_service.notifications.dto.supplierhiservice.HINotificationToSupplierContentConverter;
import net.catena_x.btp.hi.oem.backend.hi_service.notifications.dto.supplierhiservice.items.HealthIndicatorInput;
import net.catena_x.btp.hi.oem.common.model.dto.calculation.HICalculation;
import net.catena_x.btp.hi.oem.common.model.dto.calculation.HICalculationTable;
import net.catena_x.btp.hi.oem.common.model.enums.CalculationStatus;
import net.catena_x.btp.hi.oem.util.exceptions.OemHIException;
import net.catena_x.btp.libraries.edc.EdcApi;
import net.catena_x.btp.libraries.edc.util.exceptions.EdcException;
import net.catena_x.btp.libraries.notification.dto.Notification;
import net.catena_x.btp.libraries.oem.backend.model.dto.vehicle.Vehicle;
import net.catena_x.btp.libraries.util.datahelper.DataHelper;
import net.catena_x.btp.libraries.util.exceptions.BtpException;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Component
public class HIDataCollector {
    public final static String DATA_VERSION = "DV_0.0.99";

    @Autowired private HINotificationToSupplierContentConverter hiNotificationToSupplierContentConverter;
    @Autowired private HINotificationCreator hiNotificationCreator;
    @Autowired private HICollectorOptionReader hiCollectorOptionReader;
    @Autowired private HIVehicleCollector hiVehicleCollector;
    @Autowired private HIInputDataBuilder hiInputDataBuilder;
    @Autowired private HICalculationTable hiCalculationTable;
    @Autowired private HIVehicleRegistrator vehicleRegistrator;
    @Autowired private EdcApi edcApi;
    @Autowired private ObjectMapper objectMapper;

    @Value("${supplier.hiservice.inputAssetName}") private String inputAssetName;
    @Value("${supplier.hiservice.endpoint}") private URL supplierHiServiceEndpoint;

    private final Logger logger = LoggerFactory.getLogger(HIDataCollector.class);

    public void doUpdate() throws OemHIException {
        doUpdate(new HIUpdateOptions());
    }

    public void doUpdate(@Nullable final String option) throws OemHIException {
        doUpdate(hiCollectorOptionReader.read(option));
    }

    public synchronized void doUpdate(@NotNull final HIUpdateOptions options) throws OemHIException {
        final long syncCounterMin = getLastSyncCounterReady() + 1L;

        try {
            final List<Vehicle> updatedVehicles = hiVehicleCollector.collect(syncCounterMin);
            if (updatedVehicles == null) {
                logger.info("No updated vehicles this time!");
                return;
            }

            logger.info("Found " + updatedVehicles.size() + " updated vehicles!");

            registerNewVehicles(updatedVehicles);

            buildHIServiceInputsAndDispatch(updatedVehicles, syncCounterMin, options);
        } catch (final Exception exception) {
            throw new OemHIException(exception);
        }
    }

    private void registerNewVehicles(@NotNull final List<Vehicle> updatedVehicles) throws BtpException {
        vehicleRegistrator.registerNewVehicles(updatedVehicles);
    }

    private void buildHIServiceInputsAndDispatch(@NotNull final List<Vehicle> updatedVehicles,
                                                 @NotNull final long syncCounterMin,
                                                 @NotNull final HIUpdateOptions options) throws BtpException {
        final String requestId = generateRequestId();
        final DataToSupplierContent dataToSupplierContent = hiInputDataBuilder.build(requestId, updatedVehicles);
        createNewCalculationInDatabase(requestId, syncCounterMin, getSyncCounterMax(updatedVehicles));
        dispatchRequestWithHttp(requestId, dataToSupplierContent, options);
    }

    private long getSyncCounterMax(@NotNull final List<Vehicle> updatedVehicles) {
        if(DataHelper.isNullOrEmpty(updatedVehicles)){
            return -1;
        }

        final Vehicle vehicleMaxSyncCounter = updatedVehicles.stream().max(
                Comparator.comparing(Vehicle::getSyncCounter)).get();

        return vehicleMaxSyncCounter.getSyncCounter();
    }

    @NotNull
    private String generateRequestId() {
        return UUID.randomUUID().toString();
    }

    private long getLastSyncCounterReady() throws OemHIException {
        List<HICalculation> calculations = hiCalculationTable.getByStatusOrderByCalculationSyncCounterNewTransaction(
                CalculationStatus.READY);

        if(DataHelper.isNullOrEmpty(calculations)) {
            return 0;
        }

        return calculations.get(calculations.size() - 1).getCalculationSyncCounterMax();
    }

    private void createNewCalculationInDatabase(@NotNull final String requestId,
                                                @NotNull final long syncCounterMin,
                                                @NotNull final long syncCounterMax) throws OemHIException {
        hiCalculationTable.createNowExternalTransaction(requestId, syncCounterMin, syncCounterMax);
    }

    private Notification<DataToSupplierContent> prepareNotification(
            @NotNull final String requestId, @NotNull final DataToSupplierContent dataToSupplierContent,
            @NotNull final HIUpdateOptions options) throws OemHIException {

        try {
            final HINotificationToSupplierContentDAO healthIndicatorServiceInputDAO =
                    hiNotificationToSupplierContentConverter.toDAO(dataToSupplierContent);

            Notification<DataToSupplierContent> notification =
                    hiNotificationCreator.createForHttp(requestId, dataToSupplierContent);

            if (options.isLimitVehicleTwinCount()) {
                limitVehicles(notification, options.getMaxVehicleTwins());
            }

            return notification;
        } catch (final Exception exception) {
            setCalculationStatus(requestId, CalculationStatus.FAILED_INTERNAL_BUILD_REQUEST);
            throw new OemHIException(exception);
        }
    }

    private void dispatchRequestWithHttp(@NotNull final String requestId,
                                         @NotNull final DataToSupplierContent dataToSupplierContent,
                                         @NotNull final HIUpdateOptions options) throws OemHIException {
        final Notification<DataToSupplierContent> notification = prepareNotification(
                requestId, dataToSupplierContent, options);

        logger.info("Request for Id " + requestId + " (" + notification.getContent().getHealthIndicatorInputs().size()
                + " vehicles) prepared.");

        processResult(requestId,
                options.isRenameLoadSpectrumToLoadCollective()
                ? renameAndCallService(requestId, notification)
                : callService(requestId, notification));
    }

    private void processResult(@NotNull final String requestId, @NotNull final ResponseEntity<String> result)
            throws OemHIException {

        if (result.getStatusCode() == HttpStatus.OK) {
            logger.info("Request for Id " + requestId + " started.");
            setCalculationStatus(requestId, CalculationStatus.RUNNING);
        } else {
            serviceCallFailed("Starting request for Id \" + requestId + \" failed: http code "
                    + result.getStatusCode().toString() + ", response body: " + result.getBody().toString());
        }
    }

    private ResponseEntity<String> renameAndCallService(
            @NotNull final String requestId, @NotNull final Notification<DataToSupplierContent> notification)
            throws OemHIException {

        try {
            final String notificationAsString = objectMapper.writeValueAsString(notification)
                                                                .replace("Spectrum", "Collective");

            return startAsyncRequest(requestId, supplierHiServiceEndpoint.toString(),
                    inputAssetName, notificationAsString, String.class);
        } catch(final IOException exception) {
            setCalculationStatus(requestId, CalculationStatus.FAILED_EXTERNAL);
            throw new OemHIException("Error while converting inputs to json!", exception);
        }
    }

    private ResponseEntity<String> callService(
            @NotNull final String requestId, @NotNull final Notification<DataToSupplierContent> notification)
            throws OemHIException {
        return startAsyncRequest(requestId, supplierHiServiceEndpoint.toString(), inputAssetName,
                notification, String.class);
    }

    private void serviceCallFailed(@NotNull final String errorText) throws OemHIException {
        logger.error(errorText);
        throw new OemHIException(errorText);
    }

    private void limitVehicles(@NotNull final Notification<DataToSupplierContent> notificationInOut,
                               @NotNull final int maxVehicleTwins) {
        if(maxVehicleTwins < notificationInOut.getContent().getHealthIndicatorInputs().size()) {

            List<HealthIndicatorInput> limitedInputs =
                    notificationInOut.getContent().getHealthIndicatorInputs().subList(0, maxVehicleTwins);

            notificationInOut.getContent().setHealthIndicatorInputs(limitedInputs);
        }
    }

    private void setCalculationStatus(@NotNull final String requestId, @NotNull final CalculationStatus newStatus)
            throws OemHIException {
        hiCalculationTable.updateStatusNewTransaction(requestId, newStatus);
    }

    public <BodyType, ResponseType> ResponseEntity<ResponseType> startAsyncRequest(
            @NotNull final String requestId, @NotNull final String endpoint, @NotNull final String asset,
            @NotNull final BodyType messageBody, @NotNull Class<ResponseType> responseTypeClass) throws OemHIException {

        try {
            return edcApi.post(HttpUrl.parse(endpoint), asset, responseTypeClass, messageBody, new HttpHeaders());
        } catch (final EdcException exception) {
            setCalculationStatus(requestId, CalculationStatus.FAILED_EXTERNAL);
            throw new OemHIException(exception);
        }
    }
}