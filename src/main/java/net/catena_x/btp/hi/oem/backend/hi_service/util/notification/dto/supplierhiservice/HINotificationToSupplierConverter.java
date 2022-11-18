package net.catena_x.btp.hi.oem.backend.hi_service.util.notification.dto.supplierhiservice;

import net.catena_x.btp.hi.oem.backend.hi_service.util.notification.dao.supplierhiservice.HINotificationToSupplierContentDAO;
import net.catena_x.btp.libraries.notification.dao.NotificationDAO;
import net.catena_x.btp.libraries.notification.dto.Notification;
import net.catena_x.btp.libraries.notification.dto.items.NotificationHeaderConverter;
import net.catena_x.btp.libraries.oem.backend.database.rawdata.dao.base.converter.DAOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
public class HINotificationToSupplierConverter
        extends DAOConverter<NotificationDAO<HINotificationToSupplierContentDAO>,
                                        Notification<HINotificationToSupplierContent>> {

    @Autowired HINotificationToSupplierContentConverter HINotificationToSupplierContentConverter;
    @Autowired NotificationHeaderConverter notificationHeaderConverter;

    protected Notification<HINotificationToSupplierContent> toDTOSourceExists(
            @NotNull final NotificationDAO<HINotificationToSupplierContentDAO> source) {

        return new Notification<HINotificationToSupplierContent>(
                notificationHeaderConverter.toDTO(source.getHeader()),
                HINotificationToSupplierContentConverter.toDTO(source.getContent()));
    }

    protected NotificationDAO<HINotificationToSupplierContentDAO> toDAOSourceExists(
            @NotNull final Notification<HINotificationToSupplierContent> source) {

        return new NotificationDAO<HINotificationToSupplierContentDAO>(
                notificationHeaderConverter.toDAO(source.getHeader()),
                HINotificationToSupplierContentConverter.toDAO(source.getContent()));
    }
}
