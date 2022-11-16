package net.catena_x.btp.hi.supplier.data.input;

import java.util.List;

public record HealthIndicatorServiceInput(
        String requestRefId,
        List<HealthIndicatorInput> healthIndicatorInputs
) {}
