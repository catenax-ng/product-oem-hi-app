package net.catena_x.btp.hi.oem.backend.hi_service.controller;

import net.catena_x.btp.hi.oem.backend.hi_service.controller.util.HIJobRunner;
import net.catena_x.btp.hi.oem.backend.hi_service.notifications.dto.supplierhiservice.HINotificationFromSupplierContent;
import net.catena_x.btp.hi.oem.backend.hi_service.receiver.HIResultProcessor;
import net.catena_x.btp.hi.oem.util.exceptions.OemHIException;
import net.catena_x.btp.libraries.notification.dto.Notification;
import net.catena_x.btp.libraries.oem.backend.datasource.model.api.ApiResult;
import net.catena_x.btp.libraries.util.apihelper.ApiHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
public class HIController {
    public static final String COLLECTOR_API_BASEPATH = "/hidatacollector";
    public static final String RECEIVER_API_BASEPATH = "/hidatareceiver";

    @Autowired private ApiHelper apiHelper;
    @Autowired private HIJobRunner jobRunner;
    @Autowired private HIResultProcessor resultProcessor;

    private final Logger logger = LoggerFactory.getLogger(HIController.class);

    @GetMapping(COLLECTOR_API_BASEPATH + "/run")
    public ResponseEntity<ApiResult> run() {
        return jobRunner.startJob(null);
    }

    @GetMapping(COLLECTOR_API_BASEPATH + "/runtest/{options}")
    public ResponseEntity<ApiResult> run(@PathVariable @NotNull final String options) {
        return jobRunner.startJob(options);
    }

    @GetMapping(COLLECTOR_API_BASEPATH + "/setstate")
    public ResponseEntity<ApiResult> pauseResume() {
        // TODO read parameter and set execution state
        return apiHelper.failed("Setting state is not implemented!");
    }

    @GetMapping(COLLECTOR_API_BASEPATH + "/resetqueue")
    public ResponseEntity<ApiResult> resetQueue() {
        return jobRunner.resetQueue();
    }

    @PostMapping(RECEIVER_API_BASEPATH + "/notifyresult")
    public ResponseEntity<ApiResult> notifyResult(
            @RequestBody @NotNull Notification<HINotificationFromSupplierContent> result) {

        final Runnable setJobFinishedAndStartQueued = () -> {
                    final ResponseEntity<ApiResult> nextJobResponse = jobRunner.setJobFinishedAndStartQueued();
                    if(nextJobResponse.getStatusCode() != HttpStatus.OK) {
                        logger.error("Starting queued job failed: " + nextJobResponse.getBody().message());
                    }
                };

        resultProcessor.process(result, setJobFinishedAndStartQueued);
        return apiHelper.ok("Processing results started.");
    }

    @GetMapping(RECEIVER_API_BASEPATH + "/resetdatabase")
    public ResponseEntity<ApiResult> resetDatabase() {
        // TODO implement hi database reset.
        return apiHelper.failed("Database reset is not implemented!");
    }

    public ResponseEntity<ApiResult> setJobFinishedStartWaiting() throws OemHIException {
        return jobRunner.setJobFinishedAndStartQueued();
    }

    private ResponseEntity<ApiResult> waiting() {
        return apiHelper.ok("External hi calculation will be started after current running job.");
    }
}
