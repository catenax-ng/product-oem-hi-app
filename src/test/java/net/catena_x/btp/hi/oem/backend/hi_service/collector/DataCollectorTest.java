package net.catena_x.btp.hi.oem.backend.hi_service.collector;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.catena_x.btp.hi.oem.backend.hi_service.OemHiBackendServiceApplication;
import net.catena_x.btp.hi.oem.backend.hi_service.handler.HealthIndicatorResultHandler;
import net.catena_x.btp.hi.supplier.data.input.HealthIndicatorInput;
import net.catena_x.btp.libraries.oem.backend.database.rawdata.dao.VehicleTable;
import net.catena_x.btp.libraries.oem.backend.database.rawdata.model.TelemetricsData;
import net.catena_x.btp.libraries.oem.backend.database.rawdata.model.Vehicle;
import net.catena_x.btp.libraries.oem.backend.database.util.OemDatabaseException;
import net.catena_x.btp.libraries.oem.backend.util.EDCHandler;
import net.catena_x.btp.libraries.oem.backend.util.S3Handler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;



@ActiveProfiles(profiles = "hibackendservice")
@ContextConfiguration(classes = {OemHiBackendServiceApplication.class})
@EnableAutoConfiguration
@TestPropertySource(locations = {"classpath:test-hibackendservice.properties"})
@ComponentScan(basePackages = {"net.catena_x.btp.hi.oem.backend.hi_service.collector",
        "net.catena_x.btp.libraries.oem.backend.database.rawdata",
        "net.catena_x.btp.libraries.oem.backend.util",
        "net.catena_x.btp.hi.oem.backend.hi_service.handler"
})
@EntityScan(basePackages = {"net.catena_x.btp.libraries.oem.backend.database.rawdata.model"})
//@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest
class DataCollectorTest {

    @InjectMocks
    @Autowired
    private DataCollector collector;

    @Mock
    private HealthIndicatorResultHandler resultHandler;
    @Mock
    private EDCHandler edcHandler;
    @Mock
    private S3Handler s3Handler;
    @Mock
    private VehicleTable vehicleTable;

    @Autowired ObjectMapper om;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.openMocks(this);
    }

    String readFromResourceFile(String filename) throws IOException {
        return new String(Objects.requireNonNull(this.getClass().getResourceAsStream(filename))
                .readAllBytes(), StandardCharsets.UTF_8);
    }

    @Test
    void testDoUpdateSuccess() throws OemDatabaseException {
        // Setup

    }

    @Test
    void testConvert() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, IOException {
        TelemetricsData input = new TelemetricsData();
        List<double[]> adaptionValues = new ArrayList<>();
        adaptionValues.add(new double[] {20.0, 40.0, 20.0, 40.0});
        List<String> loadCollectives = new ArrayList<>();
        loadCollectives.add(readFromResourceFile("/load-collective-1.json"));
        input.setId("urn:example-telemetrics");
        input.setVehicleId("urn:uuid:2343245-2442-2344-2345423");
        input.setCreationTimestamp(Instant.parse("2007-12-31T00:00:00.00Z"));
        input.setMileage(76543.0f);
        input.setOperatingSeconds(4615200);
        input.setStorageTimestamp(Instant.parse("2022-10-12T08:17:18.734Z"));
        input.setAdaptionValues(adaptionValues);
        input.setLoadCollectives(loadCollectives);

        HealthIndicatorInput result = (HealthIndicatorInput) getConvertMethod().invoke(collector, input);

        HealthIndicatorInput expected = om.readValue(
                readFromResourceFile("/convert-expected-result-1.json"), HealthIndicatorInput.class
        );

        // comparing the objects directly fails because of the nested primitive arrays
        assertEquals(om.writeValueAsString(result), om.writeValueAsString(expected));
    }

    private Method getConvertMethod() throws NoSuchMethodException {
        Method method = DataCollector.class.getDeclaredMethod("convert", TelemetricsData.class);
        Objects.requireNonNull(method);
        method.setAccessible(true);
        return method;
    }
}