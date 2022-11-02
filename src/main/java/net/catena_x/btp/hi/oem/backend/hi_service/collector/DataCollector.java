package net.catena_x.btp.hi.oem.backend.hi_service.collector;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.catena_x.btp.hi.supplier.data.input.AdaptionValueList;
import net.catena_x.btp.hi.supplier.data.input.ClassifiedLoadCollective;
import net.catena_x.btp.hi.supplier.data.input.HealthIndicatorInput;
import net.catena_x.btp.hi.supplier.data.input.HealthIndicatorInputJson;
import net.catena_x.btp.libraries.oem.backend.database.rawdata.dao.VehicleTable;
import net.catena_x.btp.libraries.oem.backend.database.rawdata.model.TelemetricsData;
import net.catena_x.btp.libraries.oem.backend.database.rawdata.model.Vehicle;
import net.catena_x.btp.libraries.oem.backend.database.util.OemDatabaseException;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataCollector {

    @Autowired VehicleTable vehicleTable;

    final static Instant earliestTimestamp = Instant.parse("2007-12-03T10:15:30.00Z");

    Instant lastUpdate = earliestTimestamp;      // TODO should this be made persistent somehow?

    @Autowired private ObjectMapper mapper;

    public void doUpdate() throws OemDatabaseException {

        // var timestamp = getTimestampFromDb();

        List<Vehicle> updatedVehicles = doRequest();

        // lastTimestamp = timestamp;

        HealthIndicatorInputJson healthIndicatorInputJson = buildJson(updatedVehicles);

        // Normal way
            // delegate upload to S3Handler
            // delegate sending request to EDCHandler
        // Alternative for testing
            // delegate sending request to EDCHandler (http)
    }

    private List<Vehicle> doRequest() throws OemDatabaseException {
        return vehicleTable.getUpdatedSince(lastUpdate);
    }

    private HealthIndicatorInputJson buildJson(List<Vehicle> queriedVehicles) {
        List<HealthIndicatorInput> healthIndicatorInputs = new ArrayList<>();
        for(var vehicle: queriedVehicles) {

        }
        throw new NotImplementedException("Waiting for finalization of data format!");
    }

    private HealthIndicatorInput convert(TelemetricsData telemetricsData) throws OemDatabaseException {
        // List has only one element!
        List<String> loadCollectives = telemetricsData.getLoadCollectives();
        List<double[]> adaptionValues = telemetricsData.getAdaptionValues();
        verifyInput(loadCollectives, adaptionValues);

        String componentId = telemetricsData.getVehicleId();    // TODO is this correct?
        ClassifiedLoadCollective classifiedLoadCollective = convertLoadCollective(loadCollectives.get(0));
        AdaptionValueList adaptionValueList = convertAdaptionValues(telemetricsData);

        return new HealthIndicatorInput(componentId, classifiedLoadCollective, adaptionValueList);
    }

    private void verifyInput(List<String> loadCollectives, List<double[]> adaptionValues) throws OemDatabaseException {
        if(loadCollectives.size() != 1) {
            throw new OemDatabaseException("Found more than one LoadCollective! " +
                    "Data format seems to have changed!");
        }
        if(adaptionValues.size() != 1) {    // TODO is this correct?
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
        return new AdaptionValueList(
                "VER_AV_001",           // TODO should this be queried from InfoTable?
                telemetricsData.getStorageTimestamp(),
                telemetricsData.getMileage(),
                telemetricsData.getOperatingSeconds(),
                telemetricsData.getAdaptionValues().get(0)          // TODO is this correct?
        );
    }
}