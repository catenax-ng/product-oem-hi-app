package net.catena_x.btp.hi.oem.backend.hi_service.collector;

import net.catena_x.btp.hi.oem.backend.hi_service.collector.util.DataCollectorQueue;
import net.catena_x.btp.hi.oem.backend.hi_service.collector.util.DataCollectorQueueElement;
import net.catena_x.btp.hi.oem.backend.util.exceptions.HIBackendException;
import net.catena_x.btp.libraries.oem.backend.datasource.model.api.ApiResult;
import net.catena_x.btp.libraries.util.apihelper.ApiHelper;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
public class DataCollectorJobRunner {
    @Autowired private DataCollector dataCollector;
    @Autowired private DataCollectorQueue queue;
    @Autowired private ApiHelper apiHelper;

    public ResponseEntity<ApiResult> startJob() {
        try {
            return startJobIntern(queue.addNewJobToQueue(null));
        } catch (HIBackendException exception) {
            return apiHelper.failed(exception.getMessage());
        }
    }

    public ResponseEntity<ApiResult> startJob(@Nullable final String options) {
        if(options == null) {
            return startJob();
        }

        try {
            return startJobIntern(queue.addNewJobToQueue(options.toUpperCase()));
        } catch (HIBackendException exception) {
            return apiHelper.failed(exception.getMessage());
        }
    }

    public void startJobIntern(@Nullable final String options) throws HIBackendException {
        try {
            dataCollector.doUpdate(options);
        } catch (Exception exception) {
            try {
                setJobFinishedStartWaiting();
            } catch (Exception nestedException) {
                throw new HIBackendException("Start of job and queued job failed: "
                        + exception.getMessage() + nestedException.getMessage());
            }

            throw new HIBackendException(exception);
        }
    }

    public ResponseEntity<ApiResult> setJobFinishedStartWaiting() throws HIBackendException {
        return startJobIntern(queue.removeJobFromQueueReturnNextQueuedElement());
    }

    public ResponseEntity<ApiResult> resetQueue() {
        try {
            //Removing queued and runnig element (if existing).
            queue.removeJobFromQueueReturnNextQueuedElement();
            queue.removeJobFromQueueReturnNextQueuedElement();
            return apiHelper.ok("Queue is reset!");
        } catch (HIBackendException exception) {
            return apiHelper.failed(exception.getMessage());
        }
    }

    private ResponseEntity<ApiResult> startJobIntern(@NotNull final DataCollectorQueueElement currentQueueElement)
            throws HIBackendException {

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
                startJobIntern(currentQueueElement.options());
                return apiHelper.ok("Started external hi calculation.");
            }

            default: {
                throw unknownError();
            }
        }
    }

    private HIBackendException unknownError() {
        return new HIBackendException("External hi calculation cannot be started. Unknown error!");
    }
}
