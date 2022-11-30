package net.catena_x.btp.hi.supplier.mockup;

import net.catena_x.btp.hi.oem.backend.hi_service.notifications.dao.supplierhiservice.HINotificationFromSupplierContentDAO;
import net.catena_x.btp.hi.oem.backend.hi_service.notifications.dao.supplierhiservice.items.HealthIndicatorOutputDAO;
import net.catena_x.btp.hi.oem.backend.hi_service.notifications.dto.supplierhiservice.DataToSupplierContent;
import net.catena_x.btp.hi.oem.backend.hi_service.notifications.dto.supplierhiservice.items.HealthIndicatorInput;
import net.catena_x.btp.libraries.notification.dto.Notification;
import net.catena_x.btp.libraries.notification.dto.items.NotificationHeader;
import net.catena_x.btp.libraries.oem.backend.datasource.model.api.ApiResult;
import net.catena_x.btp.libraries.oem.backend.datasource.provider.util.exceptions.DataProviderException;
import net.catena_x.btp.libraries.util.apihelper.ApiHelper;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
public class HIServiceControllerSupplierMock {
    @Autowired ApiHelper apiHelper;
    @Autowired private RestTemplate restTemplate;

    @PostMapping("api/service/{assetid}/submodel")
    public ResponseEntity<ApiResult> runCalculationMock(
            @RequestBody @NotNull Notification<DataToSupplierContent> data,
            @PathVariable @NotNull final String assetid) {

        final List<HealthIndicatorOutputDAO> outputs =
                new ArrayList<>(data.getContent().getHealthIndicatorInputs().size());

        for (final HealthIndicatorInput inputData : data.getContent().getHealthIndicatorInputs()) {
            outputs.add(new HealthIndicatorOutputDAO("DV_0.0.99", inputData.getComponentId(),
                    new double[]{ 0.4, 1.1 }));
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
                Thread.sleep(10000L);
            } catch (InterruptedException exception) {
            }

            final HttpUrl requestUrl = HttpUrl.parse("http://localhost:25553/")
                    .newBuilder().addPathSegment("hidatareceiver").addPathSegment("notifyresult").build();

            final HttpHeaders headers = generateDefaultHeaders();

            final HttpEntity<Notification<HINotificationFromSupplierContentDAO>> request =
                    new HttpEntity<>(notification, headers);

            final ResponseEntity<ApiResult> response = restTemplate.postForEntity(
                    requestUrl.toString(), request, ApiResult.class);
        }).start();

        return apiHelper.accepted("Accepted!");
    }

    protected HttpHeaders generateDefaultHeaders() {
        final HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    protected <T> void checkResponse(@Nullable final ResponseEntity<ApiResult> response)
            throws DataProviderException {

        if(response == null) {
            throw new DataProviderException("Internal error using data updater api!");
        }
        else if(response.getStatusCode() != HttpStatus.OK
                && response.getStatusCode() != HttpStatus.CREATED
                && response.getStatusCode() != HttpStatus.ACCEPTED) {
            String message = null;
            if(response.getBody() != null) {
                if(response.getBody().message() != null) {
                    message = response.getBody().message();
                }
            }

            throw new DataProviderException("Http status not ok while using data updater api: "
                    + ((message!=null) ? message : "Unknown error!"));
        }
    }
}
