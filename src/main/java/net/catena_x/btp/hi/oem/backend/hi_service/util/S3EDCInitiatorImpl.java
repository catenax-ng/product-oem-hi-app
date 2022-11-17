package net.catena_x.btp.hi.oem.backend.hi_service.util;

import net.catena_x.btp.libraries.oem.backend.util.S3EDCInitiator;
import net.catena_x.btp.libraries.oem.backend.util.S3EDCRequestMapper;
import org.springframework.stereotype.Component;

@Component
public class S3EDCInitiatorImpl implements S3EDCInitiator {
    public void startAsyncRequest(String requestId, String endpoint, String messageBody) {

    }
}
