package net.catena_x.btp.hi.oem.backend.hi_service.controller;

import net.catena_x.btp.hi.oem.backend.hi_service.collector.util.HICollectorOptionReader;
import net.catena_x.btp.hi.oem.backend.hi_service.collector.util.HIUpdateOptions;
import net.catena_x.btp.hi.oem.backend.hi_service.controller.util.HIDbMaintainer;
import net.catena_x.btp.hi.oem.backend.hi_service.controller.util.HIJobRunner;
import net.catena_x.btp.hi.oem.util.exceptions.OemHIException;
import net.catena_x.btp.libraries.util.apihelper.ApiHelper;
import net.catena_x.btp.libraries.util.apihelper.model.DefaultApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(HIBackendApiConfig.COLLECTOR_API_PATH_BASE)
public class HIBackendCollectorControllerRunTest {
    @Autowired private HIJobRunner jobRunner;
    @Autowired private HIDbMaintainer hiDbMaintainer;
    @Autowired private HICollectorOptionReader hiCollectorOptionReader;
    @Autowired private ApiHelper apiHelper;

    @GetMapping(value = "/runtest/{options}", produces = "application/json")
    public ResponseEntity<DefaultApiResult> run(@PathVariable @NotNull final String options) {

        HIUpdateOptions updateOptions = null;
        try {
            updateOptions = hiCollectorOptionReader.read(options);
        } catch(final OemHIException exception) {
            return apiHelper.failed(exception.getMessage());
        }

        if(updateOptions.isForceCalculationIgnoringQueue()) {
            jobRunner.resetQueue();
        }

        if(updateOptions.isResetHiDatabase()) {
            hiDbMaintainer.reset();
        }

        return jobRunner.startJob(updateOptions);
    }
}
