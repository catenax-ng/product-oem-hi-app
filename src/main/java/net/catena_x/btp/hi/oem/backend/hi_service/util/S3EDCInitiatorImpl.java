package net.catena_x.btp.hi.oem.backend.hi_service.util;

import net.catena_x.btp.libraries.edc.EdcApi;
import net.catena_x.btp.libraries.oem.backend.edc.S3EDCInitiator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class S3EDCInitiatorImpl implements S3EDCInitiator {
    @Autowired EdcApi edcApi;

    public void startAsyncRequest(String requestId, String endpoint, String messageBody) {

    }
}
