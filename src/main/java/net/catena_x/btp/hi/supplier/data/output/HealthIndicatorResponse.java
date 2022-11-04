package net.catena_x.btp.hi.supplier.data.output;

import java.util.List;

public record HealthIndicatorResponse(
        String requestRefId,
        List<HealthIndicatorOutput> healthIndicatorOutputs
) {
}
