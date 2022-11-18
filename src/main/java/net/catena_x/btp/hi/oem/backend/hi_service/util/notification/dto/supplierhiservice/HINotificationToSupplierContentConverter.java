package net.catena_x.btp.hi.oem.backend.hi_service.util.notification.dto.supplierhiservice;

import net.catena_x.btp.hi.oem.backend.hi_service.util.notification.dao.supplierhiservice.HINotificationToSupplierContentDAO;
import net.catena_x.btp.hi.oem.backend.hi_service.util.notification.dto.supplierhiservice.items.HealthIndicatorInputConverter;
import net.catena_x.btp.libraries.oem.backend.database.rawdata.dao.base.converter.DAOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
public class HINotificationToSupplierContentConverter
        extends DAOConverter<HINotificationToSupplierContentDAO, HINotificationToSupplierContent> {

    @Autowired
    HealthIndicatorInputConverter healthIndicatorInputConverter;

    protected HINotificationToSupplierContent toDTOSourceExists(
            @NotNull final HINotificationToSupplierContentDAO source) {

        return new HINotificationToSupplierContent(
                source.getRequestRefId(),
                healthIndicatorInputConverter.toDTO(source.getHealthIndicatorInputs()));
    }

    protected HINotificationToSupplierContentDAO toDAOSourceExists(
            @NotNull final HINotificationToSupplierContent source) {

        return new HINotificationToSupplierContentDAO(
                source.getRequestRefId(),
                healthIndicatorInputConverter.toDAO(source.getHealthIndicatorInputs()));
    }
}