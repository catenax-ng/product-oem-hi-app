package net.catena_x.btp.hi.oem.backend.hi_service.collector;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.errors.MinioException;
import net.catena_x.btp.hi.oem.backend.hi_service.util.HIS3EDCRequestMetadata;
import net.catena_x.btp.hi.oem.backend.hi_service.util.S3EDCRequestMapperInMemory;
import net.catena_x.btp.hi.supplier.data.input.AdaptionValueList;
import net.catena_x.btp.hi.supplier.data.input.HealthIndicatorInput;
import net.catena_x.btp.hi.supplier.data.input.HealthIndicatorServiceInput;
import net.catena_x.btp.libraries.bamm.custom.adaptionvalues.AdaptionValues;
import net.catena_x.btp.libraries.bamm.custom.classifiedloadspectrum.ClassifiedLoadSpectrum;
import net.catena_x.btp.libraries.bamm.custom.classifiedloadspectrum.items.LoadSpectrumType;
import net.catena_x.btp.libraries.oem.backend.model.dto.infoitem.InfoTable;
import net.catena_x.btp.libraries.oem.backend.model.dto.vehicle.VehicleTable;
import net.catena_x.btp.libraries.oem.backend.model.dto.telematicsdata.TelematicsData;
import net.catena_x.btp.libraries.oem.backend.model.dto.vehicle.Vehicle;
import net.catena_x.btp.libraries.oem.backend.database.util.exceptions.OemDatabaseException;
import net.catena_x.btp.libraries.oem.backend.model.enums.InfoKey;
import net.catena_x.btp.hi.oem.backend.hi_service.util.S3EDCInitiatorImpl;
import net.catena_x.btp.libraries.oem.backend.util.S3Handler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Component
public class DataCollector {

    @Autowired private VehicleTable vehicleTable;
    @Autowired private InfoTable infoTable;
    @Autowired private S3EDCInitiatorImpl s3EDCInitiator;
    @Autowired private S3EDCRequestMapperInMemory edcRequestMapper;
    @Autowired private ObjectMapper mapper;
    @Autowired private S3Handler s3Handler;
    @Value("${cloud.inputFile.key}") private String key;
    @Value("${zf.hiservice.endpoint}") private URL suplierHiServiceEndpoint;
    @Value("${zf.hiservice.inputAssetName}") private String inputAssetName;

    private long lastCounter = -1;      // TODO should this be made persistent somehow?

    Logger logger = LoggerFactory.getLogger(DataCollector.class);

    public void doUpdate() throws OemDatabaseException, IOException, MinioException, NoSuchAlgorithmException,
            InvalidKeyException {
        List<Vehicle> updatedVehicles = collectUpdatedVehicles();
        setNewestCounterIfNewVehicles(updatedVehicles);
        if(updatedVehicles.size() == 0) {
            logger.info("No updated vehicles this time!");
            return;
        }
        String requestId = getRequestId();
        logger.info("Found " + updatedVehicles.size() + " updated vehicles!");
        HealthIndicatorServiceInput healthIndicatorServiceInput =
                buildHealthIndicatorInputJson(requestId, updatedVehicles);
        storeRequest(requestId);
        dispatchRequestWithS3(requestId, healthIndicatorServiceInput);
    }

    @NotNull
    private String getRequestId() {
        return UUID.randomUUID().toString();
    }

    private void storeRequest(String requestId) {
        edcRequestMapper.storePendingRequest(requestId, new HIS3EDCRequestMetadata(lastCounter));
    }


    private void uploadToS3(HealthIndicatorServiceInput inputFile) throws IOException, MinioException,
            NoSuchAlgorithmException, InvalidKeyException {
        String resultJson = mapper.writeValueAsString(inputFile);
        s3Handler.uploadFileToS3(resultJson, key);
    }

    private void dispatchRequestWithS3(String requestId,
                                       HealthIndicatorServiceInput healthIndicatorServiceInput)
            throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException {
        uploadToS3(healthIndicatorServiceInput);
        s3EDCInitiator.startAsyncRequest(requestId, suplierHiServiceEndpoint.toString(),
                generateNotificationBody());
    }

    private List<Vehicle> collectUpdatedVehicles() throws OemDatabaseException {
        return vehicleTable.getSyncCounterSinceNewTransaction(lastCounter);
    }

    private void setNewestCounterIfNewVehicles(List<Vehicle> result) {
        Optional<Vehicle> maxCounterVehicle = result.stream().max(Comparator.comparing(Vehicle::getSyncCounter));
        maxCounterVehicle.ifPresent(vehicle -> lastCounter = vehicle.getSyncCounter());
    }

    private HealthIndicatorServiceInput buildHealthIndicatorInputJson(String requestId, List<Vehicle> queriedVehicles) throws OemDatabaseException {
        List<HealthIndicatorInput> healthIndicatorInputs = new ArrayList<>();
        for(var vehicle: queriedVehicles) {
            TelematicsData telemetrics = vehicle.getNewestTelematicsData();
            healthIndicatorInputs.add(convert(telemetrics, vehicle.getGearboxId()));
        }
        return new HealthIndicatorServiceInput(requestId, healthIndicatorInputs);
    }

    private HealthIndicatorInput convert(TelematicsData telematicsData,
                                         String componentId) throws OemDatabaseException {
        // List has only one element!
        List<ClassifiedLoadSpectrum> loadSpectra = telematicsData.getLoadSpectra();
        List<AdaptionValues> adaptionValues = telematicsData.getAdaptionValues();
        verifyInput(loadSpectra, adaptionValues);

        ClassifiedLoadSpectrum classifiedLoadSpectrum = findLoadSpectrum(
                loadSpectra, LoadSpectrumType.CLUTCH);
        AdaptionValueList adaptionValueList = convertAdaptionValues(adaptionValues.get(0));

        return new HealthIndicatorInput(componentId, classifiedLoadSpectrum, adaptionValueList);
    }

    private void verifyInput(List<ClassifiedLoadSpectrum> loadSpectra,
                             List<AdaptionValues> adaptionValues) throws OemDatabaseException {
        if(loadSpectra.size() != 1) {
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

    private AdaptionValueList convertAdaptionValues(@NotNull final AdaptionValues adaptionValues)
            throws OemDatabaseException {

        String version = "DV_0.0.99";

        // assert version is correct
        if(!infoTable.getInfoValueNewTransaction(InfoKey.DATAVERSION).equals(version)) {
            throw new OemDatabaseException("Data Version has changed!");
        }

        return new AdaptionValueList(
                version,
                adaptionValues.getStatus().getDate(),
                adaptionValues.getStatus().getMileage(),
                (long)(Float.parseFloat(adaptionValues.getStatus().getOperatingTime()) * 3600.0f),
                adaptionValues.getValues()
        );
    }

    private String generateNotificationBody() {
        // TODO this should generate the message that tells ZF which asset to request for input data
        return inputAssetName;      // for testing purposes
    }
}