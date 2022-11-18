package net.catena_x.btp.hi.oem.backend.hi_service.util;

import net.catena_x.btp.hi.oem.backend.hi_service.receiver.HIResultReceiver;
import net.catena_x.btp.hi.oem.backend.hi_service.util.notification.dao.supplierhiservice.HINotificationFromSupplierContentDAO;
import net.catena_x.btp.hi.oem.backend.hi_service.util.notification.dto.supplierhiservice.HINotificationFromSupplierConverter;
import net.catena_x.btp.libraries.notification.dao.NotificationDAO;
import net.catena_x.btp.libraries.oem.backend.edc.S3EDCResponseDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.constraints.NotNull;

@Controller
public class S3EDCResponseDispatcherImpl implements S3EDCResponseDispatcher<HINotificationFromSupplierContentDAO> {

    @Autowired private HIResultReceiver hiResultReceiver;
    @Autowired private HINotificationFromSupplierConverter hiNotificationFromSupplierConverter;

    @Override
    @PostMapping("/edc/notification")
    public ResponseEntity<String> receiveNotification(
            @RequestBody @NotNull final NotificationDAO<HINotificationFromSupplierContentDAO> notificationBody) {
        //System.out.println(notificationBody);
        hiResultReceiver.processHealthIndicatorResponse(hiNotificationFromSupplierConverter.toDTO(notificationBody));

        return ResponseEntity.ok().build();
    }
}
