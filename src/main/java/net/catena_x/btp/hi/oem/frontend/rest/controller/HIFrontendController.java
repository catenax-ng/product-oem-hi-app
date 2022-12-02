package net.catena_x.btp.hi.oem.frontend.rest.controller;

import net.catena_x.btp.hi.oem.backend.hi_service.receiver.HIResultProcessor;
import net.catena_x.btp.hi.oem.common.model.dto.vehicle.HIVehicleTable;
import net.catena_x.btp.hi.oem.frontend.model.dao.vehicle.HIFVehicle;
import net.catena_x.btp.hi.oem.frontend.model.dao.vehicle.HIFVehicleConverter;
import net.catena_x.btp.hi.oem.frontend.model.dao.vehicle.statistics.HIFHealthIndicatorDistributionsConverter;
import net.catena_x.btp.hi.oem.frontend.model.dao.vehicle.statistics.HIFHistogramConverter;
import net.catena_x.btp.hi.oem.frontend.model.dto.statistics.HIHealthIndicatorDistributions;
import net.catena_x.btp.hi.oem.frontend.model.dto.statistics.HIHistogram;
import net.catena_x.btp.hi.oem.frontend.model.enums.HIFHealthState;
import net.catena_x.btp.hi.oem.util.exceptions.OemHIException;
import net.catena_x.btp.libraries.util.apihelper.ApiHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
public class HIFrontendController {
    public static final String API_BASEPATH = "/frontend";

    @Autowired ApiHelper apiHelper;
    @Autowired HIFVehicleConverter hifVehicleConverter;
    @Autowired HIVehicleTable hiVehicleTable;
    @Autowired HIFHealthIndicatorDistributionsConverter hifHealthIndicatorDistributionsConverter;
    @Autowired HIFHistogramConverter hifHistogramConverter;

    private final Logger logger = LoggerFactory.getLogger(HIResultProcessor.class);

    @GetMapping(value=API_BASEPATH + "/vehicle/id/{vehicleId}", produces = "application/json")
    public ResponseEntity<String> getVehicleById(@PathVariable @NotNull final String vehicleId) {
        try {
            return apiHelper.okAsString(hifVehicleConverter.toDAO(
                    hiVehicleTable.getByIdWithHealthIndicatorsNewTransaction(vehicleId)));
        } catch (final OemHIException exception) {
            logger.error(exception.getMessage());
            return apiHelper.failedAsString(exception.getMessage());
        }
    }

    @GetMapping(value=API_BASEPATH + "/vehicle/van/{van}", produces = "application/json")
    public ResponseEntity<String> getVehicleByVan(@PathVariable @NotNull final String van) {
        try {
            return apiHelper.okAsString(hifVehicleConverter.toDAO(
                    hiVehicleTable.getByVanWithHealthIndicatorsNewTransaction(van)));
        } catch (final OemHIException exception) {
            logger.error(exception.getMessage());
            return apiHelper.failedAsString(exception.getMessage());
        }
    }

    @GetMapping(value=API_BASEPATH + "/vehicle/gearboxid/{gearboxId}", produces = "application/json")
    public ResponseEntity<String> getVehicleBygearboxId(@PathVariable @NotNull final String gearboxId) {
        try {
            return apiHelper.okAsString(hifVehicleConverter.toDAO(
                    hiVehicleTable.getByGearboxIdWithHealthIndicatorsNewTransaction(gearboxId)));
        } catch (final OemHIException exception) {
            logger.error(exception.getMessage());
            return apiHelper.failedAsString(exception.getMessage());
        }
    }

    @GetMapping(value=API_BASEPATH + "/statistic/healthstates/distributions", produces = "application/json")
    public ResponseEntity<String> getDistributions() {
        try {
            final List<HIFVehicle> vehicles = hifVehicleConverter.toDAO(
                    hiVehicleTable.getAllWithHealthIndicatorsNewTransaction());

            final HIHealthIndicatorDistributions distributions = new HIHealthIndicatorDistributions();
            distributions.setHistogramLoadSpectra(generateHistogramHealthStatesLoadSpectra(vehicles));
            distributions.setHistogramLoadSpectra(generateHistogramHealthStatesAdaptionValues(vehicles));
            return apiHelper.okAsString(hifHealthIndicatorDistributionsConverter.toDAO(distributions));
        } catch (final OemHIException exception) {
            logger.error(exception.getMessage());
            return apiHelper.failedAsString(exception.getMessage());
        }
    }

    @GetMapping(value=API_BASEPATH + "/statistic/healthstates/distribution/{type}", produces = "application/json")
    public ResponseEntity<String> getDistribution(@PathVariable @NotNull final String type) {
        try {
            final List<HIFVehicle> vehicles = hifVehicleConverter.toDAO(
                    hiVehicleTable.getAllWithHealthIndicatorsNewTransaction());
            return apiHelper.okAsString(hifHistogramConverter.toDAO(
               switch (type) {
                   case "loadspectra" -> generateHistogramHealthStatesLoadSpectra(vehicles);
                   case "adaptionvalues" -> generateHistogramHealthStatesAdaptionValues(vehicles);
                   default -> throw new OemHIException("Invalid type " + type);
               }
            ));
        } catch (final OemHIException exception) {
            logger.error(exception.getMessage());
            return apiHelper.failedAsString(exception.getMessage());
        }
    }

    private HIHistogram generateHistogramHealthStatesLoadSpectra(@NotNull final List<HIFVehicle> vehicles ) {
        return generateHistogram(vehicles.stream().map(v->v.getHealthStateLoadSpectra()).toList());
    }

    private HIHistogram generateHistogramHealthStatesAdaptionValues(@NotNull final List<HIFVehicle> vehicles ) {
        return generateHistogram(vehicles.stream().map(v->v.getHealthStateAdaptionValues()).toList());
    }

    private HIHistogram generateHistogram(@NotNull final List<HIFHealthState> healthStates) {
        int[] counts = new int[4];

        for (final HIFHealthState healthState : healthStates) {
            ++counts[switch(healthState) {
                case GREEN -> 0;
                case YELLOW -> 1;
                case RED -> 2;
                case CALCULATION_PENDING -> 3;
            }];
        }

        return new HIHistogram(counts[0], counts[1], counts[2], counts[3]);
    }
}
