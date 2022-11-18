package net.catena_x.btp.hi.oem.backend.hi_service.util;

import net.catena_x.btp.hi.oem.backend.hi_service.receiver.HIResultReceiver;
import net.catena_x.btp.libraries.oem.backend.util.S3EDCResponseDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class S3EDCResponseDispatcherImpl implements S3EDCResponseDispatcher {

    @Autowired private HIResultReceiver hiResultReceiver;

    @Override
    @PostMapping("/edc/notification")
    public ResponseEntity<String> receiveNotification(String notificationBody) {
        System.out.println(notificationBody);
        return ResponseEntity.ok().build();
    }
}
