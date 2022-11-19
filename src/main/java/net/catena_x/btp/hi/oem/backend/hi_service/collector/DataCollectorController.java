package net.catena_x.btp.hi.oem.backend.hi_service.collector;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// TODO all endpoints need to be authenticated!

@Controller
@RequestMapping("/datacollector")
public class DataCollectorController {

    @Autowired private DataCollector dataCollector;
    private boolean runningRequest = false;
    private boolean startNextImmediately = false;

    @GetMapping("/run")
    public ResponseEntity<String> run() {
        if(!runningRequest) {
            try {
                dataCollector.doUpdate();
                return ResponseEntity.ok("Job started!");
            }
            catch (Exception e) {       // TODO refine!
                return ResponseEntity.internalServerError()
                        .body("Execution of update failed!");
            }
        }
        else if(!startNextImmediately) {
            startNextImmediately = true;
            return ResponseEntity.ok("Job enqueued!");
        }
        else {
            // TODO what is a good return code for this case?
            return ResponseEntity.internalServerError()
                    .body("Job already running and next job is scheduled!");
        }

    }

    @GetMapping("/setstate")
    public ResponseEntity<String> pause_resume() {
        // TODO read parameter and set execution state
        throw new NotImplementedException();
    }
}

