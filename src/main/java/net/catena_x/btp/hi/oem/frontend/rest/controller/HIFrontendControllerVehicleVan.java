package net.catena_x.btp.hi.oem.frontend.rest.controller;

import net.catena_x.btp.hi.oem.backend.hi_service.receiver.HIResultProcessor;
import net.catena_x.btp.hi.oem.common.model.dto.vehicle.HIVehicleTable;
import net.catena_x.btp.hi.oem.frontend.model.dao.vehicle.HIFVehicleConverter;
import net.catena_x.btp.hi.oem.util.exceptions.OemHIException;
import net.catena_x.btp.libraries.util.apihelper.ApiHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(HIFrontendApiConfig.API_PATH_BASE)
public class HIFrontendControllerVehicleVan {
    @Autowired ApiHelper apiHelper;
    @Autowired HIFVehicleConverter hifVehicleConverter;
    @Autowired HIVehicleTable hiVehicleTable;

    private final Logger logger = LoggerFactory.getLogger(HIResultProcessor.class);

    @GetMapping(value = "/vehicle/van/{van}", produces = "application/json")
    public ResponseEntity<String> getVehicleByVan(@PathVariable @NotNull final String van) {
        try {
            return apiHelper.okAsString(hifVehicleConverter.toDAO(
                    hiVehicleTable.getByVanWithHealthIndicatorsNewTransaction(van)));
        } catch (final OemHIException exception) {
            logger.error(exception.getMessage());
            return apiHelper.failedAsString(exception.getMessage());
        }
    }
}
