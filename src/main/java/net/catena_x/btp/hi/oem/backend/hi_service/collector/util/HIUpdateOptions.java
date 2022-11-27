package net.catena_x.btp.hi.oem.backend.hi_service.collector.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HIUpdateOptions {
    private boolean renameLoadSpectrumToLoadCollective = false;
    private boolean limitVehicleTwinCount = false;
    private int maxVehicleTwins = -1;

    public boolean appliesChanges() {
        return renameLoadSpectrumToLoadCollective || limitVehicleTwinCount;
    }
}