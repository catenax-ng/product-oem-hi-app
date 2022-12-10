package net.catena_x.btp.hi.oem.backend.hi_service.controller;

import net.catena_x.btp.libraries.util.apihelper.ApiHelper;
import net.catena_x.btp.libraries.util.apihelper.model.DefaultApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(HIBackendApiConfig.COLLECTOR_API_PATH_BASE)
public class HIBackendCollectorControllerSetState {
    @Autowired private ApiHelper apiHelper;

    @GetMapping(value = "/setstate", produces = "application/json")
    public ResponseEntity<DefaultApiResult> pauseResume() {
        // TODO read parameter and set execution state
        return apiHelper.failed("Setting state is not implemented!");
    }
}
