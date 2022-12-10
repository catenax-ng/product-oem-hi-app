package net.catena_x.btp.hi.oem.frontend.rest.controller;

import net.catena_x.btp.hi.oem.backend.hi_service.receiver.HIResultProcessor;
import net.catena_x.btp.hi.oem.common.model.dto.vehicle.HIVehicleTable;
import net.catena_x.btp.hi.oem.frontend.model.dao.vehicle.HIFVehicle;
import net.catena_x.btp.hi.oem.frontend.model.dao.vehicle.HIFVehicleConverter;
import net.catena_x.btp.hi.oem.frontend.model.dao.vehicle.statistics.HIFHealthIndicatorDistributionsConverter;
import net.catena_x.btp.hi.oem.frontend.model.dto.statistics.HIHealthIndicatorDistributions;
import net.catena_x.btp.hi.oem.frontend.rest.controller.util.HIHistogramGenerator;
import net.catena_x.btp.hi.oem.util.exceptions.OemHIException;
import net.catena_x.btp.libraries.util.apihelper.ApiHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(HIFrontendApiConfig.API_PATH_BASE)
public class HIFrontendControllerStatisticHealthStatesDistributions {
    @Autowired ApiHelper apiHelper;
    @Autowired HIFVehicleConverter hifVehicleConverter;
    @Autowired HIVehicleTable hiVehicleTable;
    @Autowired HIFHealthIndicatorDistributionsConverter hifHealthIndicatorDistributionsConverter;
    @Autowired
    HIHistogramGenerator hiHistogramGenerator;

    private final Logger logger = LoggerFactory.getLogger(HIResultProcessor.class);

    @GetMapping(value = "/statistic/healthstates/distributions", produces = "application/json")
    public ResponseEntity<String> getDistributions() {
        try {
            final List<HIFVehicle> vehicles = hifVehicleConverter.toDAO(
                    hiVehicleTable.getAllWithHealthIndicatorsNewTransaction());

            final HIHealthIndicatorDistributions distributions = new HIHealthIndicatorDistributions();
            distributions.setHistogramLoadSpectra(
                    hiHistogramGenerator.generateHistogramHealthStatesLoadSpectra(vehicles));
            distributions.setHistogramLoadSpectra(
                    hiHistogramGenerator.generateHistogramHealthStatesAdaptionValues(vehicles));
            return apiHelper.okAsString(hifHealthIndicatorDistributionsConverter.toDAO(distributions));
        } catch (final OemHIException exception) {
            logger.error(exception.getMessage());
            return apiHelper.failedAsString(exception.getMessage());
        }
    }
}
