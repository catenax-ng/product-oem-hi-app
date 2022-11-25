package net.catena_x.btp.hi.oem.common.model.dto.healthindicators;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HealthIndicators {
    long calculationSyncCounter;
    double[] values;
}
