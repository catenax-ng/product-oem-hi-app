package net.catena_x.btp.hi.oem.backend.hi_service.controller;

import net.catena_x.btp.hi.oem.backend.hi_service.collector.util.HIUpdateOptions;
import net.catena_x.btp.hi.oem.backend.hi_service.controller.util.HIJobRunner;
import net.catena_x.btp.libraries.util.apihelper.model.DefaultApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(HIBackendApiConfig.COLLECTOR_API_PATH_BASE)
public class HIBackendCollectorControllerRun {
    @Autowired private HIJobRunner jobRunner;

    @GetMapping(value = "/run", produces = "application/json")
    public ResponseEntity<DefaultApiResult> run() {
        return jobRunner.startJob(new HIUpdateOptions());
    }
}
