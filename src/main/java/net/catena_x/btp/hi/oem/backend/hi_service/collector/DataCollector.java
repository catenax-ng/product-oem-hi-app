package net.catena_x.btp.hi.oem.backend.hi_service.collector;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.catena_x.btp.hi.oem.backend.hi_service.util.HIS3EDCRequestMetadata;
import net.catena_x.btp.hi.oem.backend.hi_service.util.S3EDCRequestMapperInMemory;
import net.catena_x.btp.hi.oem.backend.hi_service.util.notification.dao.supplierhiservice.HINotificationToSupplierContentDAO;
import net.catena_x.btp.hi.oem.backend.hi_service.util.notification.dto.supplierhiservice.HINotificationToSupplierContent;
import net.catena_x.btp.hi.oem.backend.hi_service.util.notification.dto.supplierhiservice.HINotificationToSupplierContentConverter;
import net.catena_x.btp.hi.oem.backend.hi_service.util.notification.dto.supplierhiservice.HINotificationToSupplierConverter;
import net.catena_x.btp.hi.oem.backend.hi_service.util.notification.dto.supplierhiservice.items.AdaptionValuesList;
import net.catena_x.btp.hi.oem.backend.hi_service.util.notification.dto.supplierhiservice.items.HealthIndicatorInput;
import net.catena_x.btp.hi.oem.backend.hi_service.util.notification.enums.NotificationClassification;
import net.catena_x.btp.libraries.bamm.custom.adaptionvalues.AdaptionValues;
import net.catena_x.btp.libraries.bamm.custom.classifiedloadspectrum.ClassifiedLoadSpectrum;
import net.catena_x.btp.libraries.bamm.custom.classifiedloadspectrum.items.LoadSpectrumType;
import net.catena_x.btp.libraries.edc.util.exceptions.EdcException;
import net.catena_x.btp.libraries.notification.dto.Notification;
import net.catena_x.btp.libraries.notification.dto.items.NotificationHeader;
import net.catena_x.btp.libraries.notification.enums.NFSeverity;
import net.catena_x.btp.libraries.notification.enums.NFStatus;
import net.catena_x.btp.libraries.oem.backend.model.dto.infoitem.InfoTable;
import net.catena_x.btp.libraries.oem.backend.model.dto.vehicle.VehicleTable;
import net.catena_x.btp.libraries.oem.backend.model.dto.telematicsdata.TelematicsData;
import net.catena_x.btp.libraries.oem.backend.model.dto.vehicle.Vehicle;
import net.catena_x.btp.libraries.oem.backend.database.util.exceptions.OemDatabaseException;
import net.catena_x.btp.libraries.oem.backend.model.enums.InfoKey;
import net.catena_x.btp.hi.oem.backend.hi_service.util.S3EDCInitiatorImpl;
import net.catena_x.btp.libraries.oem.backend.cloud.S3Handler;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Component
public class DataCollector {

    @Autowired private VehicleTable vehicleTable;
    @Autowired private InfoTable infoTable;
    @Autowired private S3EDCInitiatorImpl s3EDCInitiator;
    @Autowired private S3EDCRequestMapperInMemory edcRequestMapper;
    @Autowired private ObjectMapper mapper;
    @Autowired private S3Handler s3Handler;
    @Autowired private HINotificationToSupplierContentConverter hiNotificationToSupplierContentConverter;
    @Autowired private HINotificationToSupplierConverter hiNotificationToSupplierConverter;
    @Value("${cloud.inputFile.key}") private String key;
    @Value("${supplier.hiservice.endpoint}") private URL suplierHiServiceEndpoint;
    @Value("${supplier.bpn}") private String supplierBpn;
    @Value("${supplier.hiservice.inputAssetName}") private String inputAssetName;
    @Value("${edc.bpn}") private String edcBpn;
    @Value("${edc.endpoint}") private URL edcEndpoint;

    private long lastCounter = -1;      // TODO should this be made persistent somehow?

    Logger logger = LoggerFactory.getLogger(DataCollector.class);

    public void doUpdate() throws OemDatabaseException, EdcException, NoSuchAlgorithmException, // MinioException,
            InvalidKeyException {
        final List<Vehicle> updatedVehicles = collectUpdatedVehicles();
        setNewestCounterIfNewVehicles(updatedVehicles);
        if(updatedVehicles.size() == 0) {
            logger.info("No updated vehicles this time!");
            return;
        }
        final String requestId = getRequestId();
        logger.info("Found " + updatedVehicles.size() + " updated vehicles!");
        final HINotificationToSupplierContent healthIndicatorServiceInput =
                buildHealthIndicatorInputJson(requestId, updatedVehicles);
        storeRequest(requestId);
        //dispatchRequestWithS3(requestId, healthIndicatorServiceInput);
        dispatchRequestWithHttp(requestId, healthIndicatorServiceInput);
    }

    @NotNull
    private String getRequestId() {
        return UUID.randomUUID().toString();
    }

    private void storeRequest(String requestId) {
        edcRequestMapper.storePendingRequest(requestId, new HIS3EDCRequestMetadata(lastCounter));
    }


    private void uploadToS3(final HINotificationToSupplierContent inputFile) throws IOException, // MinioException,
            NoSuchAlgorithmException, InvalidKeyException {
        String resultJson = mapper.writeValueAsString(inputFile);
        s3Handler.uploadFileToS3(resultJson, key);
    }

    private void dispatchRequestWithHttp(final String requestId,
                                         final HINotificationToSupplierContent hiNotificationToSupplierContent)
            throws EdcException {

        final HINotificationToSupplierContentDAO healthIndicatorServiceInputDAO =
                hiNotificationToSupplierContentConverter.toDAO(hiNotificationToSupplierContent);

        Notification<HINotificationToSupplierContent> notification =
                generateNotificationBodyForHttp(requestId, hiNotificationToSupplierContent);

        //TODO Implement response class.
        final ResponseEntity<String> result = s3EDCInitiator.startAsyncRequest(requestId, suplierHiServiceEndpoint.toString(),
                inputAssetName, notification, String.class);
    }

    private void dispatchRequestWithS3(final String requestId,
                                       final HINotificationToSupplierContent hiNotificationToSupplierContent)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException //, MinioException,
    {
        final HINotificationToSupplierContentDAO healthIndicatorServiceInputDAO =
                hiNotificationToSupplierContentConverter.toDAO(hiNotificationToSupplierContent);

        uploadToS3(hiNotificationToSupplierContent);
//FA: Not used        s3EDCInitiator.startAsyncRequest(requestId, suplierHiServiceEndpoint.toString(),
//                generateNotificationBodyForS3());
    }

    private List<Vehicle> collectUpdatedVehicles() throws OemDatabaseException {
        return vehicleTable.getSyncCounterSinceWithTelematicsDataNewTransaction(lastCounter);
    }

    private void setNewestCounterIfNewVehicles(final List<Vehicle> result) {
        final Optional<Vehicle> maxCounterVehicle = result.stream().max(Comparator.comparing(Vehicle::getSyncCounter));
        maxCounterVehicle.ifPresent(vehicle -> lastCounter = vehicle.getSyncCounter());
    }

    private HINotificationToSupplierContent buildHealthIndicatorInputJson(
            String requestId, List<Vehicle> queriedVehicles) throws OemDatabaseException {

        final List<HealthIndicatorInput> healthIndicatorInputs = new ArrayList<>();
        for(var vehicle: queriedVehicles) {
            final TelematicsData telematicsData = vehicle.getNewestTelematicsData();
            healthIndicatorInputs.add(convert(telematicsData, vehicle.getGearboxId()));
        }
        return new HINotificationToSupplierContent(requestId, healthIndicatorInputs);
    }

    private HealthIndicatorInput convert(final TelematicsData telematicsData,
                                         final String componentId) throws OemDatabaseException {
        // List has only one element!
        final List<ClassifiedLoadSpectrum> loadSpectra = telematicsData.getLoadSpectra();
        final List<AdaptionValues> adaptionValues = telematicsData.getAdaptionValues();
        verifyInput(loadSpectra, adaptionValues);

        final ClassifiedLoadSpectrum classifiedLoadSpectrum = findLoadSpectrum(
                loadSpectra, LoadSpectrumType.CLUTCH);
        final AdaptionValuesList adaptionValueList = convertAdaptionValues(adaptionValues.get(0));

        return new HealthIndicatorInput(componentId, classifiedLoadSpectrum, adaptionValueList);
    }

    private void verifyInput(final List<ClassifiedLoadSpectrum> loadSpectra,
                             final List<AdaptionValues> adaptionValues) throws OemDatabaseException {
        if(loadSpectra.size() < 1) {
            throw new OemDatabaseException("Found more than one LoadSpectrum! " +
                    "Data format seems to have changed!");
        }
        if(adaptionValues.size() != 1) {
            throw new OemDatabaseException("Found more than one AdaptionValue Array! " +
                    "Data format seems to have changed!");
        }
    }

    private ClassifiedLoadSpectrum findLoadSpectrum(@NotNull final List<ClassifiedLoadSpectrum> loadSpectra,
                                                    @NotNull final LoadSpectrumType componentDescription) {

        for (final ClassifiedLoadSpectrum loadSpectrum: loadSpectra) {
            if(loadSpectrum.getMetadata().getComponentDescription() == componentDescription) {
                return loadSpectrum;
            }
        }
        
        return null;
    }

    private AdaptionValuesList convertAdaptionValues(@NotNull final AdaptionValues adaptionValues)
            throws OemDatabaseException {

        final String version = "DV_0.0.99";

        // assert version is correct
        if(!infoTable.getInfoValueNewTransaction(InfoKey.DATAVERSION).equals(version)) {
            throw new OemDatabaseException("Data Version has changed!");
        }

        return new AdaptionValuesList(
                version,
                adaptionValues.getStatus().getDate(),
                adaptionValues.getStatus().getMileage(),
                (long)(Float.parseFloat(adaptionValues.getStatus().getOperatingTime()) * 3600.0f),
                adaptionValues.getValues()
        );
    }

    private String generateNotificationBodyForS3() {
        // TODO this should generate the message that tells ZF which asset to request for input data


        return inputAssetName;      // for testing purposes
    }

    private Notification<HINotificationToSupplierContent> generateNotificationBodyForHttp(
            final String requestId,
            final HINotificationToSupplierContent hiNotificationToSupplierContent) {

        final Notification<HINotificationToSupplierContent> notification = new Notification<>();

        final NotificationHeader header = new NotificationHeader();
        header.setNotificationID(requestId);
        header.setSenderBPN(edcBpn);
        header.setSenderAddress(edcEndpoint.toString());
        header.setRecipientBPN(supplierBpn);
        header.setRecipientAddress(suplierHiServiceEndpoint.toString());

        header.setClassification(NotificationClassification.HISERVICE.toString());
        header.setSeverity(NFSeverity.MINOR);

        header.setStatus(NFStatus.SENT);
        header.setTimeStamp(Instant.now());
        header.setTargetDate(header.getTimeStamp().plus(Duration.ofHours(12L)));

        notification.setHeader(header);
        notification.setContent(hiNotificationToSupplierContent);

        return notification;
    }
}