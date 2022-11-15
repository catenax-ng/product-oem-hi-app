package net.catena_x.btp.hi.oem.backend.hi_service.collector;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.catena_x.btp.hi.oem.backend.hi_service.OemHiBackendServiceApplication;
import net.catena_x.btp.hi.oem.backend.hi_service.handler.HealthIndicatorResultHandler;
import net.catena_x.btp.hi.supplier.data.input.HealthIndicatorInput;
import net.catena_x.btp.libraries.oem.backend.model.dto.infoitem.InfoTable;
import net.catena_x.btp.libraries.oem.backend.model.dto.vehicle.Vehicle;
import net.catena_x.btp.libraries.oem.backend.model.dto.vehicle.VehicleTable;
import net.catena_x.btp.libraries.oem.backend.model.dto.telematicsdata.TelematicsData;
import net.catena_x.btp.libraries.oem.backend.database.util.exceptions.OemDatabaseException;
import net.catena_x.btp.libraries.oem.backend.model.enums.InfoKey;
import net.catena_x.btp.libraries.oem.backend.util.EDCHandler;
import net.catena_x.btp.libraries.oem.backend.util.S3Handler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;




@TestPropertySource(locations = {"classpath:test-hibackendservice.properties"})
@ComponentScan(basePackages = {"net.catena_x.btp.hi.oem.backend.hi_service.collector",
        "net.catena_x.btp.libraries.oem.backend.model.dto",
        "net.catena_x.btp.libraries.oem.backend.util",
        "net.catena_x.btp.hi.oem.backend.hi_service.handler"
})
@EntityScan(basePackages = {"net.catena_x.btp.libraries.oem.backend.database.rawdata.model"})
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
class DataCollectorMockTest {

    private final String dataversion = "DV_0.0.99";

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
        // Mockito.verify()

    }

    @Test
    void testConvert() throws Exception {

        Mockito.when(infoTable.getInfoValueNewTransaction(InfoKey.DATAVERSION)).thenReturn(dataversion);

        TelematicsData input = new TelematicsData();
        List<double[]> adaptionValues = new ArrayList<>();
        adaptionValues.add(new double[] {20.0, 40.0, 20.0, 40.0});
        List<String> loadSpectra = new ArrayList<>();
        loadSpectra.add(readFromResourceFile("/load-collective-1.json"));
        input.setId("urn:example-telemetrics");
        input.setVehicleId("urn:uuid:2343245-2442-2344-2345423");
        input.setCreationTimestamp(Instant.parse("2007-12-31T00:00:00.00Z"));
        input.setMileage(76543.0f);
        input.setOperatingSeconds(4615200);
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

    /*private List<Vehicle> generateMockDatabaseResponse() {
        List<Vehicle> response = new ArrayList<>();
        Vehicle veh1 = new Vehicle();
        veh1.setVehicleId("urn-test1");
    }*/
}