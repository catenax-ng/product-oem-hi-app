package net.catena_x.btp.hi.supplier.data.input;

import net.catena_x.btp.libraries.bamm.custom.classifiedloadspectrum.ClassifiedLoadSpectrum;

public record HealthIndicatorInput(
        String componentId,
        ClassifiedLoadSpectrum classifiedLoadSpectrum,
        AdaptionValueList adaptionValueList
) {}
