package net.catena_x.btp.hi.supplier.data.output;

public record HealthIndicatorOutput(
        String version,
        String componentId,
        double[] healthIndicatorValues
) {
}
