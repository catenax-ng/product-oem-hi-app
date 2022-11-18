package net.catena_x.btp.hi.oem.backend.hi_service.receiver;

import net.catena_x.btp.hi.oem.backend.hi_service.util.notification.dto.supplierhiservice.HINotificationFromSupplierContent;
import net.catena_x.btp.libraries.notification.dto.Notification;
import org.springframework.stereotype.Component;

@Component
public class HIResultReceiver {
    public void processHealthIndicatorResponse(Notification<HINotificationFromSupplierContent> response) {
        // TODO
    }
}
