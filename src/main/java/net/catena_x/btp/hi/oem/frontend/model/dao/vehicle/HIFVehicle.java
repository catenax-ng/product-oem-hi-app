package net.catena_x.btp.hi.oem.frontend.model.dao.vehicle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.catena_x.btp.hi.oem.frontend.model.enums.HIFHealthState;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HIFVehicle {
    private String vehicleId;
    private String van;
    private String gearboxId;
    private Instant productionDate;
    private Instant updateTimestamp;
    private HIFHealthState healthStateLoadSpectra;
    private HIFHealthState healthStateAdaptionValues;
}
