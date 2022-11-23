package net.catena_x.btp.hi.oem.backend.hi_service.notifications.dao.supplierhiservice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.catena_x.btp.hi.oem.backend.hi_service.notifications.dao.supplierhiservice.items.HealthIndicatorOutputDAO;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class HINotificationFromSupplierContentDAO {
    private String requestRefId;
    private List<HealthIndicatorOutputDAO> healthIndicatorOutputs;
}
