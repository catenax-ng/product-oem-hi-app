package net.catena_x.btp.hi.oem.backend.hi_service.controller.util;

import net.catena_x.btp.hi.oem.backend.hi_service.collector.HIDataCollector;
import net.catena_x.btp.hi.oem.util.exceptions.OemHIException;
import net.catena_x.btp.libraries.oem.backend.datasource.model.api.ApiResult;
import net.catena_x.btp.libraries.util.apihelper.ApiHelper;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
public class HIJobRunner {
    @Autowired private HIDataCollector dataCollector;
    @Autowired private HIQueue queue;
    @Autowired private ApiHelper apiHelper;

    public ResponseEntity<ApiResult> startJob() {
        try {
            return startJobInternal(queue.addNewJobToQueue(null));
        } catch (OemHIException exception) {
            return apiHelper.failed(exception.getMessage());
        }
    }

    public ResponseEntity<ApiResult> startJob(@Nullable final String options) {
        if(options == null) {
            return startJob();
        }

        try {
            return startJobInternal(queue.addNewJobToQueue(options.toUpperCase()));
        } catch (OemHIException exception) {
            return apiHelper.failed(exception.getMessage());
        }
    }

    public ResponseEntity<ApiResult> setJobFinishedAndStartQueued() {
        try {
            return startJobInternal(queue.removeJobFromQueueReturnNextQueuedElement());
        } catch (OemHIException exception) {
            return apiHelper.failed(exception.getMessage());
        }
    }

    public ResponseEntity<ApiResult> resetQueue() {
        try {
            //Removing queued and running element (if existing).
            queue.removeJobFromQueueReturnNextQueuedElement();
            queue.removeJobFromQueueReturnNextQueuedElement();
            return apiHelper.ok("Queue is reset!");
        } catch (OemHIException exception) {
            return apiHelper.failed(exception.getMessage());
        }
    }

    private ResponseEntity<ApiResult> startJobInternal(@NotNull final HIQueueElement currentQueueElement)
            throws OemHIException {

        switch(currentQueueElement.queueState()) {
            case NOT_RUNNING: {
                //No action needed.
                return apiHelper.ok("Ok.");
            }

            case WAITING: {
                //No action needed.
                return apiHelper.ok("External hi calculation is queued and " +
                        "will be started after current running job.");
            }

            case RUNNING: {
                startJobInternal(currentQueueElement.options());
                return apiHelper.ok("Started external hi calculation.");
            }

            default: {
                throw unknownError();
            }
        }
    }

    private void startJobInternal(@Nullable final String options) throws OemHIException {
        try {
            dataCollector.doUpdate(options);
        } catch (Exception exception) {
            try {
                setJobFinishedAndStartQueued();
            } catch (Exception nestedException) {
                throw new OemHIException("Start of job and queued job failed: "
                        + exception.getMessage() + nestedException.getMessage());
            }

            throw new OemHIException(exception);
        }
    }

    private OemHIException unknownError() {
        return new OemHIException("External hi calculation cannot be started. Unknown error!");
    }
}
