package net.catena_x.btp.hi.oem.common.model.dto.calculation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.catena_x.btp.hi.oem.common.model.enums.CalculationStatus;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HICalculation {
    private String id;
    private Instant calculationTimestamp;
    private long calculationSyncCounterMin;
    private long calculationSyncCounterMax;
    private CalculationStatus status;
    private String message;
}
