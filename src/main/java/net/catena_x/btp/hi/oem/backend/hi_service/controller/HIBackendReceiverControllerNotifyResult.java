package net.catena_x.btp.hi.oem.backend.hi_service.controller;

import net.catena_x.btp.hi.oem.backend.hi_service.controller.swagger.ReceiverNotifyResultDoc;
import net.catena_x.btp.hi.oem.backend.hi_service.controller.util.HIJobRunner;
import net.catena_x.btp.hi.oem.backend.hi_service.notifications.dto.supplierhiservice.HINotificationFromSupplierContent;
import net.catena_x.btp.hi.oem.backend.hi_service.receiver.HIResultProcessor;
import net.catena_x.btp.libraries.notification.dto.Notification;
import net.catena_x.btp.libraries.util.apihelper.ApiHelper;
import net.catena_x.btp.libraries.util.apihelper.model.DefaultApiResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(HIBackendApiConfig.RECEIVER_API_PATH_BASE)
public class HIBackendReceiverControllerNotifyResult {
    @Autowired private ApiHelper apiHelper;
    @Autowired private HIJobRunner jobRunner;
    @Autowired private HIResultProcessor resultProcessor;

    private final Logger logger = LoggerFactory.getLogger(HIBackendReceiverControllerNotifyResult.class);

    @PostMapping(value = "/notifyresult", produces = "application/json")
    @io.swagger.v3.oas.annotations.Operation(
            summary = ReceiverNotifyResultDoc.SUMMARY, description = ReceiverNotifyResultDoc.DESCRIPTION,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = ReceiverNotifyResultDoc.BODY_DESCRIPTION, required = true,
                    content =  @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = ReceiverNotifyResultDoc.BODY_EXAMPLE_1_NAME,
                                            description = ReceiverNotifyResultDoc.BODY_EXAMPLE_1_DESCRIPTION,
                                            value = ReceiverNotifyResultDoc.BODY_EXAMPLE_1_VALUE
                                    )
                            }
                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = ReceiverNotifyResultDoc.RESPONSE_OK_DESCRIPTION,
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                    value = ReceiverNotifyResultDoc.RESPONSE_OK_VALUE
                                            )},
                                    schema = @io.swagger.v3.oas.annotations.media.Schema(
                                            implementation = DefaultApiResult.class)
                            )),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = ReceiverNotifyResultDoc.RESPONSE_ERROR_DESCRIPTION,
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                    value = ReceiverNotifyResultDoc.RESPONSE_ERROR_VALUE
                                            )},
                                    schema = @io.swagger.v3.oas.annotations.media.Schema(
                                            implementation = DefaultApiResult.class)
                            ))
            }
    )
    public ResponseEntity<DefaultApiResult> notifyResult(
            @RequestBody @NotNull Notification<HINotificationFromSupplierContent> result) {

        final Runnable setJobFinishedAndStartQueued = () -> {
            final ResponseEntity<DefaultApiResult> nextJobResponse = jobRunner.setJobFinishedAndStartQueued();
            if(nextJobResponse.getStatusCode() != HttpStatus.OK
                    && nextJobResponse.getStatusCode() != HttpStatus.CREATED
                    && nextJobResponse.getStatusCode() != HttpStatus.ACCEPTED) {
                logger.error("Starting queued job failed: " + nextJobResponse.getBody().getMessage());
            }
        };

        resultProcessor.process(result, setJobFinishedAndStartQueued);
        return apiHelper.ok("Processing results started.");
    }
}
