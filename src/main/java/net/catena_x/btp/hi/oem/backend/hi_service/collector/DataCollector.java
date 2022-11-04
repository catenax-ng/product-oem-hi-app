package net.catena_x.btp.hi.oem.backend.hi_service.collector;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.catena_x.btp.hi.oem.backend.hi_service.handler.HealthIndicatorResultHandler;
import net.catena_x.btp.hi.supplier.data.input.AdaptionValueList;
import net.catena_x.btp.hi.supplier.data.input.ClassifiedLoadCollective;
import net.catena_x.btp.hi.supplier.data.input.HealthIndicatorInput;
import net.catena_x.btp.hi.supplier.data.input.HealthIndicatorInputJson;
import net.catena_x.btp.libraries.oem.backend.database.rawdata.dao.InfoTable;
import net.catena_x.btp.libraries.oem.backend.database.rawdata.dao.VehicleTable;
import net.catena_x.btp.libraries.oem.backend.database.rawdata.model.InfoItem;
import net.catena_x.btp.libraries.oem.backend.database.rawdata.model.TelemetricsData;
import net.catena_x.btp.libraries.oem.backend.database.rawdata.model.Vehicle;
import net.catena_x.btp.libraries.oem.backend.database.util.OemDatabaseException;
import net.catena_x.btp.libraries.oem.backend.util.EDCHandler;
import net.catena_x.btp.libraries.oem.backend.util.S3Handler;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class DataCollector {

    @Autowired private VehicleTable vehicleTable;
    @Autowired private InfoTable infoTable;
    @Autowired private EDCHandler edcHandler;
    @Autowired private HealthIndicatorResultHandler resultHandler;
    @Autowired private ObjectMapper mapper;
    @Autowired private S3Handler s3Handler;

    final static Instant earliestTimestamp = Instant.parse("2007-12-03T10:15:30.00Z");

    Instant lastUpdate = earliestTimestamp;      // TODO should this be made persistent somehow?

    @Value("${aws.inputFile.bucketName}") private String bucketName;
    @Value("${aws.inputFile.key}") private String key;
    @Value("${zf.hiservice.endpoint}") private URL hiEndpoint;
    @Value("${zf.hiservice.inputAssetName}") private String inputAssetName;


    public void doUpdate() throws OemDatabaseException, IOException {
        Instant timestamp = infoTable.getCurrentDatabaseTimestamp();
        List<Vehicle> updatedVehicles = doRequest();
        lastUpdate = timestamp;
        HealthIndicatorInputJson healthIndicatorInputJson = buildJson(updatedVehicles);
        dispatchRequestWithS3(healthIndicatorInputJson);
    }

    private void uploadToS3(HealthIndicatorInputJson inputFile) throws IOException {
        String resultJson = mapper.writeValueAsString(inputFile);
        s3Handler.uploadFileToS3(resultJson, bucketName, key);
    }

    private void dispatchRequestWithS3(HealthIndicatorInputJson inputFile) throws IOException {
        uploadToS3(inputFile);
        edcHandler.startAsyncRequest(hiEndpoint.toString(), generateMessageBody(),
                resultHandler::processHealthIndicatorResponse);
    }

    private List<Vehicle> doRequest() throws OemDatabaseException {
        return vehicleTable.getUpdatedSince(lastUpdate);
    }

    private HealthIndicatorInputJson buildJson(List<Vehicle> queriedVehicles) throws OemDatabaseException {
        List<HealthIndicatorInput> healthIndicatorInputs = new ArrayList<>();
        for(var vehicle: queriedVehicles) {
            TelemetricsData telemetrics = vehicle.getTelemetricsData();
            healthIndicatorInputs.add(convert(telemetrics,
                    "ADDME"
                    //vehicle.getGearboxId()
            ));
        }
        String refId = UUID.randomUUID().toString();
        return new HealthIndicatorInputJson(refId, healthIndicatorInputs);
    }

    private HealthIndicatorInput convert(TelemetricsData telemetricsData,
                                         String componentId) throws OemDatabaseException {
        // List has only one element!
        List<String> loadCollectives = telemetricsData.getLoadCollectives();
        List<double[]> adaptionValues = telemetricsData.getAdaptionValues();
        verifyInput(loadCollectives, adaptionValues);

        ClassifiedLoadCollective classifiedLoadCollective = convertLoadCollective(loadCollectives.get(0));
        AdaptionValueList adaptionValueList = convertAdaptionValues(telemetricsData);

        return new HealthIndicatorInput(componentId, classifiedLoadCollective, adaptionValueList);
    }

    private void verifyInput(List<String> loadCollectives, List<double[]> adaptionValues) throws OemDatabaseException {
        if(loadCollectives.size() != 1) {
            throw new OemDatabaseException("Found more than one LoadCollective! " +
                    "Data format seems to have changed!");
        }
        if(adaptionValues.size() != 1) {
            throw new OemDatabaseException("Found more than one AdaptionValue Array! " +
                    "Data format seems to have changed!");
        }
    }

    private ClassifiedLoadCollective convertLoadCollective(String loadCollective) throws OemDatabaseException {

        try {
            return mapper.readValue(loadCollective, ClassifiedLoadCollective.class);
        }
        catch (JsonProcessingException e) {
            throw new OemDatabaseException("Mapping of load collective failed!");
        }
    }

    private AdaptionValueList convertAdaptionValues(TelemetricsData telemetricsData) throws OemDatabaseException {
        String version = "DV_0.0.99";

        // TODO assert version is correct
        if(!infoTable.getInfoValue(InfoItem.InfoKey.dataversion).equals(version)) {
            throw new OemDatabaseException("Data Version has changed!");
        }
        return new AdaptionValueList(
                version,
                telemetricsData.getStorageTimestamp(),
                telemetricsData.getMileage(),
                telemetricsData.getOperatingSeconds(),
                telemetricsData.getAdaptionValues().get(0)
        );
    }

    private String generateMessageBody() {
        // TODO this should generate the message that tells ZF which asset to request for input data
        return inputAssetName;      // for testing purposes
    }
}