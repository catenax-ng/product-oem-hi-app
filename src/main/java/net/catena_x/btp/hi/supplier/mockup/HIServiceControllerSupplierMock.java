package net.catena_x.btp.hi.supplier.mockup;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import net.catena_x.btp.hi.oem.backend.hi_service.notifications.dao.supplierhiservice.HINotificationFromSupplierContentDAO;
import net.catena_x.btp.hi.oem.backend.hi_service.notifications.dao.supplierhiservice.items.HealthIndicatorOutputDAO;
import net.catena_x.btp.hi.oem.backend.hi_service.notifications.dto.supplierhiservice.DataToSupplierContent;
import net.catena_x.btp.hi.oem.backend.hi_service.notifications.dto.supplierhiservice.items.HealthIndicatorInput;
import net.catena_x.btp.hi.supplier.mockup.swagger.SupplierMockUpDoc;
import net.catena_x.btp.libraries.notification.dto.Notification;
import net.catena_x.btp.libraries.notification.dto.items.NotificationHeader;
import net.catena_x.btp.libraries.util.apihelper.ApiHelper;
import net.catena_x.btp.libraries.util.apihelper.model.DefaultApiResult;
import okhttp3.HttpUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RestController
public class HIServiceControllerSupplierMock {
    @Autowired ApiHelper apiHelper;
    @Autowired private RestTemplate restTemplate;

    final double GREEN = 0.0;
    final double YELLOW = 0.95;
    final double RED = 1.1;

    final double[] GREEN_GREEN = {GREEN, GREEN};
    final double[] GREEN_YELLOW = {GREEN, YELLOW};
    final double[] GREEN_RED = {GREEN, RED};
    final double[] YELLOW_GREEN = {YELLOW, GREEN};
    final double[] YELLOW_YELLOW = {YELLOW, YELLOW};
    final double[] YELLOW_RED = {YELLOW, RED};
    final double[] RED_GREEN = {RED, GREEN};
    final double[] RED_YELLOW = {RED, YELLOW};
    final double[] RED_RED = {RED, RED};

    final double[][] healthIndicatorValues1 = {
            GREEN_GREEN, GREEN_GREEN, GREEN_GREEN, GREEN_GREEN, GREEN_GREEN,
            RED_YELLOW, GREEN_GREEN, GREEN_YELLOW, GREEN_GREEN, GREEN_GREEN,
            GREEN_GREEN, GREEN_GREEN, GREEN_GREEN, GREEN_YELLOW, GREEN_YELLOW,
            GREEN_GREEN, GREEN_GREEN, GREEN_GREEN, GREEN_GREEN, GREEN_GREEN,
            GREEN_GREEN, YELLOW_GREEN, GREEN_GREEN, YELLOW_GREEN, GREEN_GREEN,
            GREEN_GREEN, GREEN_GREEN, GREEN_GREEN, GREEN_GREEN, GREEN_GREEN};

    final double[][] healthIndicatorValues2 = {
            GREEN_GREEN, YELLOW_GREEN, GREEN_GREEN, GREEN_GREEN, GREEN_GREEN,
            RED_RED, GREEN_GREEN, GREEN_YELLOW, GREEN_GREEN, GREEN_GREEN,
            GREEN_GREEN, GREEN_GREEN, GREEN_GREEN, GREEN_YELLOW, GREEN_YELLOW,
            GREEN_GREEN, GREEN_GREEN, GREEN_GREEN, GREEN_YELLOW, GREEN_YELLOW,
            GREEN_GREEN, YELLOW_GREEN, GREEN_GREEN, YELLOW_GREEN, GREEN_RED,
            GREEN_GREEN, GREEN_GREEN, GREEN_GREEN, GREEN_GREEN, GREEN_GREEN};

    private boolean useHiValues1 = true;
    private synchronized boolean isHiValues1() {
        useHiValues1 = !useHiValues1;
        return !useHiValues1;
    }

    @PostMapping(value = "api/service/{assetid}/submodel", produces = "application/json")
    @io.swagger.v3.oas.annotations.Operation(
            summary = SupplierMockUpDoc.SUMMARY, description = SupplierMockUpDoc.DESCRIPTION,
            tags = {"MockUp"},
            parameters = @io.swagger.v3.oas.annotations.Parameter(
                    in = ParameterIn.PATH, name = SupplierMockUpDoc.ASSETID_NAME,
                    description = SupplierMockUpDoc.ASSETID_DESCRIPTION, required = true,
                    examples = {
                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = SupplierMockUpDoc.ASSETID_EXAMPLE_1_NAME,
                                    description = SupplierMockUpDoc.ASSETID_EXAMPLE_1_DESCRIPTION,
                                    value = SupplierMockUpDoc.ASSETID_EXAMPLE_1_VALUE
                            )
                    }
            ),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = SupplierMockUpDoc.BODY_DESCRIPTION, required = true,
                    content =  @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = SupplierMockUpDoc.BODY_EXAMPLE_1_NAME,
                                            description = SupplierMockUpDoc.BODY_EXAMPLE_1_DESCRIPTION,
                                            value = SupplierMockUpDoc.BODY_EXAMPLE_1_VALUE
                                    )
                            }
                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "202",
                            description = SupplierMockUpDoc.RESPONSE_OK_DESCRIPTION,
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                    value = SupplierMockUpDoc.RESPONSE_OK_VALUE
                                            )},
                                    schema = @io.swagger.v3.oas.annotations.media.Schema(
                                            implementation = DefaultApiResult.class)
                            )),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = SupplierMockUpDoc.RESPONSE_ERROR_DESCRIPTION,
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                    value = SupplierMockUpDoc.RESPONSE_ERROR_VALUE
                                            )},
                                    schema = @io.swagger.v3.oas.annotations.media.Schema(
                                            implementation = DefaultApiResult.class)
                            ))
            }
    )
    public ResponseEntity<DefaultApiResult> runCalculationMock(
            @RequestBody @NotNull Notification<DataToSupplierContent> data,
            @PathVariable @NotNull final String assetid) {

        final List<HealthIndicatorInput> healthIndicatorInputs = data.getContent().getHealthIndicatorInputs();
        healthIndicatorInputs.sort(Comparator.comparing(HealthIndicatorInput::getComponentId));

        final int count = healthIndicatorInputs.size();
        final List<HealthIndicatorOutputDAO> outputs = new ArrayList<>(count);
        final double[][] healthIndicatorValues = isHiValues1()? healthIndicatorValues1 : healthIndicatorValues2;

        for (int i = 0; i < count; i++) {
            final HealthIndicatorInput inputData = healthIndicatorInputs.get(i);
            outputs.add(new HealthIndicatorOutputDAO("DV_0.0.99", inputData.getComponentId(),
                    (i < healthIndicatorValues.length)? healthIndicatorValues[i] : GREEN_GREEN));
        }

        final Notification<HINotificationFromSupplierContentDAO> notification = new Notification<>();
        notification.setHeader(new NotificationHeader());
        notification.getHeader().setReferencedNotificationID(data.getContent().getRequestRefId());

        notification.setContent(new HINotificationFromSupplierContentDAO());
        notification.getContent().setRequestRefId(data.getContent().getRequestRefId());
        notification.getContent().setHealthIndicatorOutputs(outputs);

        new Thread(() ->
        {
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException exception) {
            }

            final HttpUrl requestUrl = HttpUrl.parse("http://localhost:25553/")
                    .newBuilder().addPathSegment("hidatareceiver").addPathSegment("notifyresult").build();

            final HttpHeaders headers = generateDefaultHeaders();

            final HttpEntity<Notification<HINotificationFromSupplierContentDAO>> request =
                    new HttpEntity<>(notification, headers);

            final ResponseEntity<DefaultApiResult> response = restTemplate.postForEntity(
                    requestUrl.toString(), request, DefaultApiResult.class);
        }).start();

        return apiHelper.accepted("Accepted!");
    }

    protected HttpHeaders generateDefaultHeaders() {
        final HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }
}
