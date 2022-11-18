package net.catena_x.btp.hi.oem.backend.hi_service.util.notification.dto.supplierhiservice.items;

import net.catena_x.btp.hi.oem.backend.hi_service.util.notification.dao.supplierhiservice.items.HealthIndicatorOutputDAO;
import net.catena_x.btp.libraries.oem.backend.database.rawdata.dao.base.converter.DAOConverter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
public class HealthIndicatorOutputConverter
        extends DAOConverter<HealthIndicatorOutputDAO, HealthIndicatorOutput> {

    protected HealthIndicatorOutput toDTOSourceExists(
            @NotNull final HealthIndicatorOutputDAO source) {

        return new HealthIndicatorOutput(
                source.getVersion(),
                source.getComponentId(),
                source.getHealthIndicatorValues());
    }

    protected HealthIndicatorOutputDAO toDAOSourceExists(
            @NotNull final HealthIndicatorOutput source) {

        return new HealthIndicatorOutputDAO(
                source.getVersion(),
                source.getComponentId(),
                source.getHealthIndicatorValues());
    }
}
