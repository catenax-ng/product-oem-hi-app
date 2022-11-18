package net.catena_x.btp.hi.oem.backend.hi_service.util.notification.dto.supplierhiservice.items;

import net.catena_x.btp.hi.oem.backend.hi_service.util.notification.dao.supplierhiservice.items.AdaptionValuesListDAO;
import net.catena_x.btp.libraries.oem.backend.database.rawdata.dao.base.converter.DAOConverter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
public class AdaptionValuesListConverter
        extends DAOConverter<AdaptionValuesListDAO, AdaptionValuesList> {

    protected AdaptionValuesList toDTOSourceExists(
            @NotNull final AdaptionValuesListDAO source) {

        return new AdaptionValuesList(
                source.getVersion(),
                source.getTimeStamp(),
                source.getMileageInKm(),
                source.getOperatingTimeInSeconds(),
                source.getValues());
    }

    protected AdaptionValuesListDAO toDAOSourceExists(
            @NotNull final AdaptionValuesList source) {

        return new AdaptionValuesListDAO(
                source.getVersion(),
                source.getTimeStamp(),
                source.getMileageInKm(),
                source.getOperatingTimeInSeconds(),
                source.getValues());
    }
}
