package net.catena_x.btp.hi.oem.backend.hi_service.util.notification.dao.supplierhiservice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.catena_x.btp.hi.oem.backend.hi_service.util.notification.dao.supplierhiservice.items.HealthIndicatorInputDAO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HINotificationToSupplierContentDAO {
    private String requestRefId;
    private List<HealthIndicatorInputDAO> healthIndicatorInputs;
}
