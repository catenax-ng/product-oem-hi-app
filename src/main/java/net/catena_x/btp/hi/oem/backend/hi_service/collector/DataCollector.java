package net.catena_x.btp.hi.oem.backend.hi_service.collector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.errors.MinioException;
import net.catena_x.btp.hi.oem.backend.hi_service.handler.HealthIndicatorResultHandler;
import net.catena_x.btp.hi.supplier.data.input.AdaptionValueList;
import net.catena_x.btp.hi.supplier.data.input.ClassifiedLoadSpectrum;
import net.catena_x.btp.hi.supplier.data.input.HealthIndicatorInput;
import net.catena_x.btp.hi.supplier.data.input.HealthIndicatorInputJson;
import net.catena_x.btp.libraries.oem.backend.model.dto.infoitem.InfoTable;
import net.catena_x.btp.libraries.oem.backend.model.dto.vehicle.VehicleTable;
import net.catena_x.btp.libraries.oem.backend.model.dto.telematicsdata.TelematicsData;
import net.catena_x.btp.libraries.oem.backend.model.dto.vehicle.Vehicle;
import net.catena_x.btp.libraries.oem.backend.database.util.exceptions.OemDatabaseException;
import net.catena_x.btp.libraries.oem.backend.model.enums.InfoKey;
import net.catena_x.btp.libraries.oem.backend.util.EDCHandler;
import net.catena_x.btp.libraries.oem.backend.util.S3Handler;
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
    @Autowired private EDCHandler edcHandler;
    @Autowired private HealthIndicatorResultHandler resultHandler;
    @Autowired private ObjectMapper mapper;
    @Autowired private S3Handler s3Handler;
    @Value("${cloud.inputFile.key}") private String key;
    @Value("${zf.hiservice.endpoint}") private URL hiEndpoint;
    @Value("${zf.hiservice.inputAssetName}") private String inputAssetName;

    private long lastCounter = -1;      // TODO should this be made persistent somehow?


    public void doUpdate() throws OemDatabaseException, IOException, MinioException, NoSuchAlgorithmException,
            InvalidKeyException {
        List<Vehicle> updatedVehicles = doRequest();
        if(updatedVehicles.size() == 0) {
            System.out.println("[DataCollector] No updated vehicles this time!");
            return;
        }
        System.out.println("[DataCollector] Found " + updatedVehicles.size() + " updated vehicles!");
        HealthIndicatorInputJson healthIndicatorInputJson = buildJson(updatedVehicles);
        dispatchRequestWithS3(healthIndicatorInputJson);
    }

    private void uploadToS3(HealthIndicatorInputJson inputFile) throws IOException, MinioException,
            NoSuchAlgorithmException, InvalidKeyException {
        String resultJson = mapper.writeValueAsString(inputFile);
        s3Handler.uploadFileToS3(resultJson, key);
    }

    private void dispatchRequestWithS3(HealthIndicatorInputJson inputFile) throws IOException, MinioException,
            NoSuchAlgorithmException, InvalidKeyException {
        uploadToS3(inputFile);
        edcHandler.startAsyncRequest(hiEndpoint.toString(), generateMessageBody(),
                 resultHandler::processHealthIndicatorResponse);
    }

    private List<Vehicle> doRequest() throws OemDatabaseException {
        var result = vehicleTable.getSyncCounterSinceNewTransaction(lastCounter);
        setNewestCounterIfNewVehicles(result);
        return result;
    }

    private void setNewestCounterIfNewVehicles(List<Vehicle> result) {
        Optional<Vehicle> maxCounterVehicle = result.stream().max(Comparator.comparing(Vehicle::getSyncCounter));
        maxCounterVehicle.ifPresent(vehicle -> lastCounter = vehicle.getSyncCounter());
    }

    private HealthIndicatorInputJson buildJson(List<Vehicle> queriedVehicles) throws OemDatabaseException {
        List<HealthIndicatorInput> healthIndicatorInputs = new ArrayList<>();
        for(var vehicle: queriedVehicles) {
            TelematicsData telemetrics = vehicle.getNewestTelematicsData();
            healthIndicatorInputs.add(convert(telemetrics, vehicle.getGearboxId()));
        }
        String refId = UUID.randomUUID().toString();
        return new HealthIndicatorInputJson(refId, healthIndicatorInputs);
    }

    private HealthIndicatorInput convert(TelematicsData telematicsData,
                                         String componentId) throws OemDatabaseException {
        // List has only one element!
        List<String> loadCollectives = telematicsData.getLoadSpectra();
        List<double[]> adaptionValues = telematicsData.getAdaptionValues();
        verifyInput(loadCollectives, adaptionValues);

        ClassifiedLoadSpectrum classifiedLoadSpectrum = convertLoadSpectrum(loadCollectives.get(0));
        AdaptionValueList adaptionValueList = convertAdaptionValues(telematicsData);

        return new HealthIndicatorInput(componentId, classifiedLoadSpectrum, adaptionValueList);
    }

    private void verifyInput(List<String> loadCollectives, List<double[]> adaptionValues) throws OemDatabaseException {
        if(loadCollectives.size() != 1) {
            throw new OemDatabaseException("Found more than one LoadSpectrum! " +
                    "Data format seems to have changed!");
        }
        if(adaptionValues.size() != 1) {
            throw new OemDatabaseException("Found more than one AdaptionValue Array! " +
                    "Data format seems to have changed!");
        }
    }

    private ClassifiedLoadSpectrum convertLoadSpectrum(String loadCollective) throws OemDatabaseException {

        try {
            return mapper.readValue(loadCollective, ClassifiedLoadSpectrum.class);
        }
        catch (JsonProcessingException e) {
            throw new OemDatabaseException("Mapping of load collective failed!");
        }
    }

    private AdaptionValueList convertAdaptionValues(TelematicsData telematicsData) throws OemDatabaseException {
        String version = "DV_0.0.99";

        // assert version is correct
        if(!infoTable.getInfoValueNewTransaction(InfoKey.DATAVERSION).equals(version)) {
            throw new OemDatabaseException("Data Version has changed!");
        }
        return new AdaptionValueList(
                version,
                telematicsData.getStorageTimestamp(),
                telematicsData.getMileage(),
                telematicsData.getOperatingSeconds(),
                telematicsData.getAdaptionValues().get(0)
        );
    }

    private String generateMessageBody() {
        // TODO this should generate the message that tells ZF which asset to request for input data
        return inputAssetName;      // for testing purposes
    }
}