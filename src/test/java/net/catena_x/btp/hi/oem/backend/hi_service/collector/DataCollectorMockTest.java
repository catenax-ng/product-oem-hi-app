package net.catena_x.btp.hi.oem.backend.hi_service.collector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.catena_x.btp.hi.oem.backend.hi_service.handler.HealthIndicatorResultHandler;
import net.catena_x.btp.hi.supplier.data.input.HealthIndicatorInput;
import net.catena_x.btp.hi.supplier.data.input.HealthIndicatorInputJson;
import net.catena_x.btp.libraries.bamm.common.BammStatus;
import net.catena_x.btp.libraries.bamm.custom.adaptionvalues.AdaptionValues;
import net.catena_x.btp.libraries.bamm.custom.classifiedloadspectrum.ClassifiedLoadSpectrum;
import net.catena_x.btp.libraries.oem.backend.model.dto.infoitem.InfoTable;
import net.catena_x.btp.libraries.oem.backend.model.dto.vehicle.Vehicle;
import net.catena_x.btp.libraries.oem.backend.model.dto.vehicle.VehicleTable;
import net.catena_x.btp.libraries.oem.backend.model.dto.telematicsdata.TelematicsData;
import net.catena_x.btp.libraries.oem.backend.model.enums.InfoKey;
import net.catena_x.btp.libraries.oem.backend.util.EDCHandler;
import net.catena_x.btp.libraries.oem.backend.util.S3Handler;
import net.catena_x.btp.libraries.util.TimeStampDeserializer;
import net.catena_x.btp.libraries.util.TimeStampSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestPropertySource(locations = {"classpath:test-hibackendservice.properties"})
@ComponentScan(basePackages = {
        "net.catena_x.btp.hi.oem.backend.hi_service.collector",
        "net.catena_x.btp.libraries.oem.backend.model.dto",
        "net.catena_x.btp.libraries.oem.backend.util",
        "net.catena_x.btp.hi.oem.backend.hi_service.handler"
})
@EntityScan(basePackages = {"net.catena_x.btp.libraries.oem.backend.database.rawdata.model"})
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
class DataCollectorMockTest {

    private final String dataversion = "DV_0.0.99";

    private final UUID testUUID = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

    @MockBean
    private HealthIndicatorResultHandler resultHandler;
    @MockBean
    private EDCHandler edcHandler;
    @MockBean
    private S3Handler s3Handler;
    @MockBean
    private VehicleTable vehicleTable;
    @MockBean
    private InfoTable infoTable;

    @Autowired
    private DataCollector collector;

    @Autowired ObjectMapper om;
    boolean objectMapperInitialized = false;

    @BeforeEach
    void beforeEach() {
        if(!objectMapperInitialized){
            om.registerModule(new JavaTimeModule());

            SimpleModule timestampSerializerModule = new SimpleModule();
            timestampSerializerModule.addSerializer(Instant.class, new TimeStampSerializer());
            om.registerModule(timestampSerializerModule);

            SimpleModule timestampDeserializerModule = new SimpleModule();
            timestampDeserializerModule.addDeserializer(Instant.class, new TimeStampDeserializer());
            om.registerModule(timestampDeserializerModule);

            objectMapperInitialized = true;
        }

        MockitoAnnotations.openMocks(this);
    }

    String readFromResourceFile(String filename) throws IOException {
        return new String(Objects.requireNonNull(this.getClass().getResourceAsStream(filename))
                .readAllBytes(), StandardCharsets.UTF_8);
    }

    @Test
    @DirtiesContext
    void testDoUpdateSuccessFirstCounter() throws Exception {
        // make the "database" return pre-defined vehicles
        Mockito.when(vehicleTable.getSyncCounterSinceNewTransaction(Mockito.anyLong())).thenReturn(
                generateMockDatabaseResponse(getCounter())
        );
        Mockito.when(infoTable.getInfoValueNewTransaction(InfoKey.DATAVERSION)).thenReturn(dataversion);

        // load expected result
        String expectedJson = om.writeValueAsString(om.readValue(readFromResourceFile("/update-expected-s3-1.json"),
                HealthIndicatorInputJson.class));

        // mock UUID class to always return the same UUID
        try (MockedStatic<UUID> mocked = Mockito.mockStatic(UUID.class)) {
            mocked.when(UUID::randomUUID).thenReturn(testUUID);
            // the actual tested method
            collector.doUpdate();
        }

        // setup assertion to test correct call of S3Handler
        Mockito.verify(s3Handler, Mockito.times(1)).uploadFileToS3(
                Mockito.argThat(
                        resultJson -> {
                            Assertions.assertEquals(expectedJson, resultJson);
                            return true;
                        }
                ),
                Mockito.argThat(x -> true)
        );

        Assertions.assertEquals(2, getCounter());
    }

    @Test
    @DirtiesContext
    void testDoUpdateNoNewVehicles() throws Exception {
        // make the "database" return pre-defined vehicles
        Mockito.when(vehicleTable.getSyncCounterSinceNewTransaction(Mockito.anyLong())).thenReturn(
                generateMockDatabaseResponse(2)
        );
        setCounter(1);
        Mockito.when(infoTable.getInfoValueNewTransaction(InfoKey.DATAVERSION)).thenReturn(dataversion);

        // mock UUID class to always return the same UUID
        try (MockedStatic<UUID> mocked = Mockito.mockStatic(UUID.class)) {
            mocked.when(UUID::randomUUID).thenReturn(testUUID);
            // the actual tested method
            collector.doUpdate();
        }

        // setup assertion to test correct call of S3Handler
        Mockito.verify(s3Handler, Mockito.never()).uploadFileToS3(
                Mockito.anyString(),
                Mockito.anyString()
        );
    }

    @Test
    void testConvert() throws Exception {

        Mockito.when(infoTable.getInfoValueNewTransaction(InfoKey.DATAVERSION)).thenReturn(dataversion);

        ClassifiedLoadSpectrum loadSpectum = om.readValue(
                readFromResourceFile("/load-collective-1.json"),
                ClassifiedLoadSpectrum.class);

        TelematicsData input = new TelematicsData();

        List<ClassifiedLoadSpectrum> loadSpectra = new ArrayList<>();
        loadSpectra.add(loadSpectum);
        List<AdaptionValues> adaptionValues = new ArrayList<>();

        AdaptionValues adaptionValueSet = new AdaptionValues();
        adaptionValueSet.setValues(new double[] {20.0, 40.0, 20.0, 40.0});
        BammStatus status = new BammStatus();
        status.setRouteDescription("Default route");
        status.setMileage(3245L);
        status.setDate(Instant.parse("2022-10-12T08:16:18.734Z"));
        status.setOperatingTime("235.2");
        adaptionValueSet.setStatus(status);
        adaptionValues.add(adaptionValueSet);

        input.setId("urn:example-telemetrics");
        input.setVehicleId("urn:uuid:2343245-2442-2344-2345423");
        input.setStorageTimestamp(Instant.parse("2022-10-12T08:17:18.734Z"));
        input.setAdaptionValues(adaptionValues);
        input.setLoadSpectra(loadSpectra);

        HealthIndicatorInput result = (HealthIndicatorInput) getConvertMethod().invoke(collector, input,
                "urn:uuid:2343245-2442-2344-2345423");

        HealthIndicatorInput expected = om.readValue(
                readFromResourceFile("/convert-expected-result-1.json"), HealthIndicatorInput.class
        );

        // comparing the objects directly fails because of the nested primitive arrays
        assertEquals(om.writeValueAsString(result), om.writeValueAsString(expected));
    }

    private Method getConvertMethod() throws NoSuchMethodException {
        Method method = DataCollector.class.getDeclaredMethod("convert", TelematicsData.class, String.class);
        Objects.requireNonNull(method);
        method.setAccessible(true);
        return method;
    }

    private List<Vehicle> generateMockDatabaseResponse(long counter) throws Exception {
        ClassifiedLoadSpectrum loadSpectum = om.readValue(
                readFromResourceFile("/load-collective-1.json"),
                ClassifiedLoadSpectrum.class);

        List<ClassifiedLoadSpectrum> loadSpectra = new ArrayList<>();
        loadSpectra.add(loadSpectum);
        List<AdaptionValues> adaptionValues = new ArrayList<>();

        AdaptionValues adaptionValueSet = new AdaptionValues();
        adaptionValueSet.setValues(new double[] {20.0, 40.0, 20.0, 40.0});
        BammStatus status = new BammStatus();
        status.setRouteDescription("Default route");
        status.setMileage(3245L);
        status.setDate(Instant.parse("2022-10-12T08:16:18.734Z"));
        status.setOperatingTime("235.2");
        adaptionValueSet.setStatus(status);
        adaptionValues.add(adaptionValueSet);

        TelematicsData t1 = new TelematicsData();
        t1.setId("t1");
        t1.setSyncCounter(2);
        t1.setVehicleId("v1");
        t1.setStorageTimestamp(Instant.parse("2022-10-12T08:17:18.734Z"));
        t1.setLoadSpectra(loadSpectra);
        t1.setAdaptionValues(adaptionValues);

        TelematicsData t2 = new TelematicsData();
        t2.setId("t2");
        t2.setSyncCounter(5);
        t2.setVehicleId("v2");
        t2.setStorageTimestamp(Instant.parse("2022-10-12T08:17:18.734Z"));
        t2.setLoadSpectra(loadSpectra);
        t2.setAdaptionValues(adaptionValues);

        List<Vehicle> response = new ArrayList<>();

        Vehicle veh1 = new Vehicle();
        veh1.setVehicleId("urn-test1");
        veh1.setNewestTelematicsData(t1);
        veh1.setSyncCounter(t1.getSyncCounter());
        veh1.setGearboxId("g1");
        veh1.setVan("van1");
        veh1.setProductionDate(Instant.parse("2012-12-31T00:00:00.00Z"));
        veh1.setUpdateTimestamp(Instant.parse("2022-10-12T08:17:18.734Z"));

        Vehicle veh2 = new Vehicle();
        veh2.setVehicleId("urn-test1");
        veh2.setNewestTelematicsData(t2);
        veh2.setSyncCounter(t2.getSyncCounter());
        veh2.setGearboxId("g2");
        veh2.setVan("van2");
        veh2.setProductionDate(Instant.parse("2012-12-31T00:00:00.00Z"));
        veh2.setUpdateTimestamp(Instant.parse("2022-10-12T08:17:18.734Z"));

        if(counter < 1) response.add(veh1);
        else if(counter < 2) response.add(veh2);

        return response;
    }

    private Field getLastCounterField() throws NoSuchFieldException {
        var field = DataCollector.class.getDeclaredField("lastCounter");
        field.setAccessible(true);
        return field;
    }

    private long getCounter() throws NoSuchFieldException, IllegalAccessException {
        return (long) getLastCounterField().get(collector);
    }

    private void setCounter(long counter) throws NoSuchFieldException, IllegalAccessException {
        getLastCounterField().set(collector, counter);
    }
}