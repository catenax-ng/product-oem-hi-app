package net.catena_x.btp.hi.supplier.data.input;

public record HealthIndicatorInput(
        String componentId,
        ClassifiedLoadSpectrum classifiedLoadSpectrum,
        AdaptionValueList adaptionValueList
) {}
