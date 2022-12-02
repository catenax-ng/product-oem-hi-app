package net.catena_x.btp.hi.oem.backend.hi_service.collector.util;

import net.catena_x.btp.hi.oem.backend.hi_service.notifications.dto.supplierhiservice.DataToSupplierContent;
import net.catena_x.btp.hi.oem.backend.hi_service.notifications.dto.supplierhiservice.items.AdaptionValuesList;
import net.catena_x.btp.hi.oem.backend.hi_service.notifications.dto.supplierhiservice.items.HealthIndicatorInput;
import net.catena_x.btp.hi.oem.util.exceptions.OemHIException;
import net.catena_x.btp.libraries.bamm.custom.classifiedloadspectrum.ClassifiedLoadSpectrum;
import net.catena_x.btp.libraries.bamm.custom.classifiedloadspectrum.items.LoadSpectrumType;
import net.catena_x.btp.libraries.oem.backend.model.dto.telematicsdata.TelematicsData;
import net.catena_x.btp.libraries.oem.backend.model.dto.vehicle.Vehicle;
import net.catena_x.btp.libraries.util.datahelper.DataHelper;
import net.catena_x.btp.libraries.util.exceptions.BtpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Component
public class HIInputDataBuilder {
    @Autowired HIAdaptionValueInputConverter hiAdaptionValueInputConverter;

    public DataToSupplierContent build(@NotNull final String requestId,
                                       @NotNull final List<Vehicle> updatedVehicles) throws OemHIException {
        final List<HealthIndicatorInput> healthIndicatorInputs = new ArrayList<>();

        try {
            for (var vehicle : updatedVehicles) {
                healthIndicatorInputs.add(convert(vehicle.getNewestTelematicsData(), vehicle.getGearboxId()));
            }
        } catch (final BtpException exception) {
            throw new OemHIException(exception);
        }

        return new DataToSupplierContent(requestId, healthIndicatorInputs);
    }

    private HealthIndicatorInput convert(@NotNull final TelematicsData telematicsData,
                                         @NotNull final String componentId) throws BtpException {

        final ClassifiedLoadSpectrum classifiedLoadSpectrum = findLoadSpectrumByType(
                telematicsData.getLoadSpectra(), LoadSpectrumType.CLUTCH);

        final AdaptionValuesList adaptionValueList = hiAdaptionValueInputConverter.convert(
                DataHelper.getFirstAndOnlyItem(telematicsData.getAdaptionValues()));

        return new HealthIndicatorInput(componentId, classifiedLoadSpectrum, adaptionValueList);
    }

    private ClassifiedLoadSpectrum findLoadSpectrumByType(
            @NotNull final List<ClassifiedLoadSpectrum> loadSpectra,
            @NotNull final LoadSpectrumType componentDescription) throws OemHIException {

        for (final ClassifiedLoadSpectrum loadSpectrum: loadSpectra) {
            if(loadSpectrum.getMetadata().getComponentDescription() == componentDescription) {
                return loadSpectrum;
            }
        }

        throw new OemHIException("Load spectrum with required type not found!");
    }
}

