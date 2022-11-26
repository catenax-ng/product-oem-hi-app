package net.catena_x.btp.hi.oem.common.database.hi.tables.vehicle;

import net.catena_x.btp.hi.oem.common.database.hi.tables.healthindicators.HIHealthIndicatorsDAO;

public record HIVehicleWithHealthindicatorsDAO(
    HIVehicleDAO vehicle,
    HIHealthIndicatorsDAO healthIndicators
) {}
