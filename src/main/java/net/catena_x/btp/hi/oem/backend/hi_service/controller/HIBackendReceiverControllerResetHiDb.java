package net.catena_x.btp.hi.oem.backend.hi_service.controller;

import net.catena_x.btp.hi.oem.backend.hi_service.controller.util.HIDbMaintainer;
import net.catena_x.btp.libraries.util.apihelper.model.DefaultApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(HIBackendApiConfig.RECEIVER_API_PATH_BASE)
public class HIBackendReceiverControllerResetHiDb {
    @Autowired
    private HIDbMaintainer hiDbMaintainer;

    @GetMapping(value = "/resethidb", produces = "application/json")
    public ResponseEntity<DefaultApiResult> resetHiDb() {
        return hiDbMaintainer.reset();
    }
}
