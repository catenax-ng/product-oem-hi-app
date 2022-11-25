package net.catena_x.btp.hi.oem.backend.hi_service.receiver;

import net.catena_x.btp.hi.oem.backend.hi_service.notifications.dto.supplierhiservice.HINotificationFromSupplierContent;
import net.catena_x.btp.hi.oem.backend.hi_service.notifications.dto.supplierhiservice.items.HealthIndicatorOutput;
import net.catena_x.btp.hi.oem.util.exceptions.OemHIException;
import net.catena_x.btp.libraries.notification.dto.Notification;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
public class HIResultProcessor {
    private final Logger logger = LoggerFactory.getLogger(HIResultProcessor.class);

    public void process(@NotNull final Notification<HINotificationFromSupplierContent> result,
                        @NotNull final Runnable notifyProcessed) {
        try {
            new Thread(() -> processingThread(result, notifyProcessed)).start();
        } catch (Exception exception) {
            logError(exception.getMessage());
            notifyProcessed.run();
        }
    }

    private void processingThread(@NotNull final Notification<HINotificationFromSupplierContent> result,
                                  @NotNull final Runnable notifyProcessed) {
        try {
            assertHeaderAndBody(result);
            this.processIntern(result.getContent(), getReferenceId(result));
        } catch (OemHIException exception) {
            logError(exception.getMessage());
        }

        notifyProcessed.run();
    }

    private void processIntern(@NotNull final HINotificationFromSupplierContent result,
                               @NotNull final String referenceId)
            throws OemHIException {

        if(result.getHealthIndicatorOutputs().isEmpty()) {
            throw new OemHIException("HI result for " + referenceId
                    + " is empty (maybe a format or calculation error occurred)!");
        }

        for (final HealthIndicatorOutput output: result.getHealthIndicatorOutputs()) {
            processSingleOutput(output, referenceId);
        }
    }

    private void assertHeaderAndBody(@Nullable final Notification<HINotificationFromSupplierContent> result)
        throws OemHIException {

        if (result == null) {
            throw new OemHIException("Notification is null!");
        }

        if (result.getHeader() == null) {
            throw new OemHIException("Notification header is not present!");
        }

        if (result.getContent() == null) {
            throw new OemHIException("Notification content is not present!");
        }
    }

    private void assertContentReferenceId(@Nullable final Notification<HINotificationFromSupplierContent> result)
            throws OemHIException {
        if (result.getContent().getRequestRefId() == null) {
            throw new OemHIException("No reference id in Notification content present!");
        }
    }

    private String getReferenceId(@NotNull final Notification<HINotificationFromSupplierContent> result)
            throws OemHIException {

        if(result.getHeader().getReferencedNotificationID() == null) {
            assertContentReferenceId(result);
            return result.getHeader().getReferencedNotificationID();
        }

        return result.getHeader().getReferencedNotificationID();
    }

    private void processSingleOutput(@NotNull final HealthIndicatorOutput output,
                                     @NotNull final String referenceId) throws OemHIException {

        //TODO: Implement writing to hi database (and remove logging).

        final StringBuilder hiValues = new StringBuilder();

        if( output.getHealthIndicatorValues().length > 0) {
            hiValues.append(output.getHealthIndicatorValues()[0]);
            for (int i = 1; i < output.getHealthIndicatorValues().length; i++) {
                hiValues.append(", ");
                hiValues.append(output.getHealthIndicatorValues()[i]);
            }
        }

        logger.info("  > " + referenceId + ": gearbox[" + output.getComponentId()
                + "]: HI={" + hiValues.toString() + "}" );
    }

    private synchronized void logError(@NotNull final String message) {
        logger.error(message);
    }
}
