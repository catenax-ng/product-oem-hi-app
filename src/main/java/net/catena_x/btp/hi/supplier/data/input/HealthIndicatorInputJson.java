package net.catena_x.btp.hi.supplier.data.input;

import java.util.List;

public record HealthIndicatorInputJson(
        String requestRefId,
        List<HealthIndicatorInput> healthIndicatorInputs
) {}
