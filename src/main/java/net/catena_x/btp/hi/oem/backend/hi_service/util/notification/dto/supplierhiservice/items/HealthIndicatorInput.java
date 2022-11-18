package net.catena_x.btp.hi.oem.backend.hi_service.util.notification.dto.supplierhiservice.items;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.catena_x.btp.libraries.bamm.custom.classifiedloadspectrum.ClassifiedLoadSpectrum;

@Getter
@Setter
@AllArgsConstructor
public class HealthIndicatorInput {
    private String componentId;
    private ClassifiedLoadSpectrum classifiedLoadSpectrum;
    private AdaptionValuesList adaptionValues;
}
