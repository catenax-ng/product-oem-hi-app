package net.catena_x.btp.hi.oem.backend.hi_service.collector;

import net.catena_x.btp.hi.oem.backend.util.exceptions.HIBackendException;
import net.catena_x.btp.libraries.oem.backend.datasource.model.api.ApiResult;
import net.catena_x.btp.libraries.util.apihelper.ApiHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/datacollector")
public class DataCollectorController {
    @Autowired private ApiHelper apiHelper;
    @Autowired private DataCollectorJobRunner jobRunner;

    @GetMapping("/run")
    public ResponseEntity<ApiResult> run() {
        return jobRunner.startJob(null);
    }

    @GetMapping("/runtest/{options}")
    public ResponseEntity<ApiResult> run(@PathVariable @NotNull final String options) {
        return jobRunner.startJob(options);
    }

    @GetMapping("/setstate")
    public ResponseEntity<ApiResult> pauseResume() {
        // TODO read parameter and set execution state
        return apiHelper.failed("Setting state is not implemented!");
    }

    @GetMapping("/resetdatabase")
    public ResponseEntity<ApiResult> resetDatabase() {
        // TODO implement hi database reset.
        return apiHelper.failed("Database reset is not implemented!");
    }

    @GetMapping("/resetqueue")
    public ResponseEntity<ApiResult> resetQueue() {
        return jobRunner.resetQueue();
    }

    public ResponseEntity<ApiResult> setJobFinishedStartWaiting() throws HIBackendException {
        return jobRunner.setJobFinishedStartWaiting();
    }

    private ResponseEntity<ApiResult> waiting() {
        return apiHelper.ok("External hi calculation will be started after current running job.");
    }
}
