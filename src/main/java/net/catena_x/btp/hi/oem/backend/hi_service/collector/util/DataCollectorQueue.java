package net.catena_x.btp.hi.oem.backend.hi_service.collector.util;

import net.catena_x.btp.hi.oem.backend.util.exceptions.HIBackendException;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class DataCollectorQueue {
    private DataCollectorQueueElement queue = new DataCollectorQueueElement(DataCollectorQueueState.NOT_RUNNING, null);

    public synchronized DataCollectorQueueElement addNewJobToQueue(@Nullable final String options)
            throws HIBackendException {

        switch(queue.queueState()) {
            case NOT_RUNNING: {
                queue = new DataCollectorQueueElement(DataCollectorQueueState.RUNNING, options);
                break;
            }

            case RUNNING: {
                queue = new DataCollectorQueueElement(DataCollectorQueueState.WAITING, options);
                break;
            }

            case WAITING: {
                assertQueuedOptionsCompatible(options);
                // No action needed.
                break;
            }

            default: {
                throw new HIBackendException("Unknown hi job queue state!");
            }
        }

        return queue;
    }

    public synchronized DataCollectorQueueElement removeJobFromQueueReturnNextQueuedElement()
            throws HIBackendException {

        switch(queue.queueState()) {
            case NOT_RUNNING: {
                // No action needed. If a job was running while the application restated, ignore the state.
                break;
            }

            case RUNNING: {
                queue = new DataCollectorQueueElement(DataCollectorQueueState.NOT_RUNNING, null);
                break;
            }

            case WAITING: {
                //Start queued element.
                queue = new DataCollectorQueueElement(DataCollectorQueueState.RUNNING, queue.options());
                break;
            }

            default: {
                throw new HIBackendException("Unknown hi job queue state!");
            }
        }

        return queue;
    }

    private void assertQueuedOptionsCompatible(@Nullable final String options) throws HIBackendException {
        if((options == null) != (queue.options() == null)) {
            throw new HIBackendException("There is already a queued job with incompatible options!");
        }

        if(options != null) {
            if (!options.equals(queue.options())) {
                throw new HIBackendException("There is already a queued job with incompatible options!");
            }
        }
    }
}
