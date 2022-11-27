package net.catena_x.btp.hi.oem.backend.hi_service.notifications.dto.supplierhiservice;

import net.catena_x.btp.hi.oem.backend.hi_service.notifications.dao.supplierhiservice.HINotificationToSupplierContentDAO;
import net.catena_x.btp.libraries.notification.dao.NotificationDAO;
import net.catena_x.btp.libraries.notification.dto.Notification;
import net.catena_x.btp.libraries.notification.dto.items.NotificationHeaderConverter;
import net.catena_x.btp.libraries.util.database.converter.DAOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
public class HINotificationToSupplierConverter
        extends DAOConverter<NotificationDAO<HINotificationToSupplierContentDAO>,
                                        Notification<DataToSupplierContent>> {

    @Autowired private HINotificationToSupplierContentConverter HINotificationToSupplierContentConverter;
    @Autowired private NotificationHeaderConverter notificationHeaderConverter;

    protected Notification<DataToSupplierContent> toDTOSourceExists(
            @NotNull final NotificationDAO<HINotificationToSupplierContentDAO> source) {

        return new Notification<DataToSupplierContent>(
                notificationHeaderConverter.toDTO(source.getHeader()),
                HINotificationToSupplierContentConverter.toDTO(source.getContent()));
    }

    protected NotificationDAO<HINotificationToSupplierContentDAO> toDAOSourceExists(
            @NotNull final Notification<DataToSupplierContent> source) {

        return new NotificationDAO<HINotificationToSupplierContentDAO>(
                notificationHeaderConverter.toDAO(source.getHeader()),
                HINotificationToSupplierContentConverter.toDAO(source.getContent()));
    }
}
