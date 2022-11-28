package net.catena_x.btp.hi.oem.frontend.webserver;

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
            final List<HIFVehicle> resultListConverted = hifVehicleConverter.toDAO(
                hiVehicleTable.getAllWithHealthIndicatorsNewTransaction());
                
            // set model attribute
            model.addAttribute("hiVehicles", resultListConverted);
            return "index";
        } catch (final OemHIException exception) {
            // TODO logging
            return "error";
        }
    }
}
