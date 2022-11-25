package net.catena_x.btp.hi.oem.common.database.hi.tables.vehicle;

import net.catena_x.btp.hi.oem.common.database.hi.tables.healthindicators.HealthIndicatorsDAO;

public record VehicleWithHealthindicatorsDAO(
    VehicleDAO vehicle,
    HealthIndicatorsDAO healthIndicators
) {}
