package net.catena_x.btp.hi.oem.frontend.webserver;

import net.catena_x.btp.hi.oem.frontend.model.enums.HIFHealthState;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import net.catena_x.btp.hi.oem.common.model.dto.vehicle.HIVehicleTable;
import net.catena_x.btp.hi.oem.frontend.model.dao.vehicle.HIFVehicle;
import net.catena_x.btp.hi.oem.frontend.model.dao.vehicle.HIFVehicleConverter;
import net.catena_x.btp.hi.oem.util.exceptions.OemHIException;

import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;
import java.util.List;

@Controller
public class HIFrontendWebinterfaceController {
    @Autowired HIFVehicleConverter hifVehicleConverter;
    @Autowired HIVehicleTable hiVehicleTable;

    /**
     * Alias for /index.html.
     */
    @GetMapping({"/", "/index.html"})
    public String start(@NotNull final Model model) {
        try {
            List<HIFVehicle> resultListConverted = hifVehicleConverter.toDAO(
                hiVehicleTable.getAllWithHealthIndicatorsNewTransaction());
            /* // Uncomment this for quick testing of the UI
            for(int i = 0; i < 100; ++i) {
                var v = new HIFVehicle();
                v.setHealthStateAdaptionValues(HIFHealthState.GREEN);
                v.setHealthStateLoadSpectra(HIFHealthState.RED);
                resultListConverted.add(v);
            }
            for(int i = 0; i < 84; ++i) {
                var v = new HIFVehicle();
                v.setHealthStateAdaptionValues(HIFHealthState.YELLOW);
                v.setHealthStateLoadSpectra(HIFHealthState.YELLOW);
                resultListConverted.add(v);
            }
            for(int i = 0; i < 56; ++i) {
                var v = new HIFVehicle();
                v.setHealthStateAdaptionValues(HIFHealthState.RED);
                v.setHealthStateLoadSpectra(HIFHealthState.GREEN);
                resultListConverted.add(v);
            }
            for(int i = 0; i < 100; ++i) {
                var v = new HIFVehicle();
                v.setHealthStateAdaptionValues(HIFHealthState.CALCULATION_PENDING);
                v.setHealthStateLoadSpectra(HIFHealthState.CALCULATION_PENDING);
                resultListConverted.add(v);
            } */
            // set model attribute
            model.addAttribute("hiVehicles", resultListConverted);
            return "index";
        } catch (final OemHIException exception) {
            // TODO logging
            return "error";
        }
    }
}
