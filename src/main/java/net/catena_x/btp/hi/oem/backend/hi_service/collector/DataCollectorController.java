package net.catena_x.btp.hi.oem.backend.hi_service.collector;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// TODO all endpoints need to be authenticated!

// TODO only execute one calculation at a time and
//  use GETDATE() on database instead of local timestamp!

@Controller
public class DataCollectorController {

    @Autowired DataCollector dataCollector;
    boolean runningRequest = false;
    boolean startNextImmediately = false;

    @GetMapping("/datacollector/run")
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

    @GetMapping("/datacollector/setstate")
    public ResponseEntity<String> pause_resume() {
        // TODO read parameter and set execution state
        throw new NotImplementedException();
    }
}

