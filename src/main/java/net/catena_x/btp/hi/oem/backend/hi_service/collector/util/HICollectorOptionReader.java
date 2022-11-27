package net.catena_x.btp.hi.oem.backend.hi_service.collector.util;

import net.catena_x.btp.hi.oem.util.exceptions.OemHIException;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
public class HICollectorOptionReader {
    public HIUpdateOptions read(@Nullable final String options) throws OemHIException {
        if(options == null) {
            return new HIUpdateOptions();
        }

        try {
            return readFromString(options.toUpperCase());
        } catch (final Exception exception) {
            throw new OemHIException("Error while reading update options!", exception);
        }
    }

    private HIUpdateOptions readFromString(@NotNull final String options) {
        HIUpdateOptions result = new HIUpdateOptions();

        if (options.length() == 0) {
            return result;
        }

        setRenameLoadSpectra(result, options);

        setLoadSpectraLimitation(result, result.isRenameLoadSpectrumToLoadCollective() ?
                options.substring(1) : options);

        return result;
    }

    private void setRenameLoadSpectra(@NotNull final HIUpdateOptions updateOptionsInOut,
                                      @NotNull final String options) {
        updateOptionsInOut.setRenameLoadSpectrumToLoadCollective(options.substring(0, 1).equals("R"));
    }

    private void setLoadSpectraLimitation(@NotNull final HIUpdateOptions updateOptionsInOut,
                                          @NotNull final String limitationAsString) {
        updateOptionsInOut.setLimitVehicleTwinCount(limitationAsString.length() > 0);
        if(updateOptionsInOut.isLimitVehicleTwinCount()) {
            updateOptionsInOut.setMaxVehicleTwins(Integer.parseInt(limitationAsString));
        }
    }
}
