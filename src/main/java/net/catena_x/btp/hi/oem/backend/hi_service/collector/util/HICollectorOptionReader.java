package net.catena_x.btp.hi.oem.backend.hi_service.collector.util;

import net.catena_x.btp.hi.oem.util.exceptions.OemHIException;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.constraints.NotNull;

@Component
public class HICollectorOptionReader {
    private Pattern pattern = Pattern.compile("[A-Z]*");

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
        final HIUpdateOptions result = new HIUpdateOptions();

        if (options.length() == 0) {
            return result;
        }

        final Matcher matcher = pattern.matcher(options);
        int lenConstants = 0;
        if(matcher.find()) {
            final String constants = matcher.group();
            lenConstants = constants.length();

            setRenameLoadSpectra(result, constants);
            setForceCalculation(result, constants);
            setRecalculateAllVehicles(result, constants);
        }

        setLoadSpectraLimitation(result, result.isRenameLoadSpectrumToLoadCollective() ?
                                                    options.substring(lenConstants) : options);

        return result;
    }

    private void setRenameLoadSpectra(@NotNull final HIUpdateOptions updateOptionsInOut,
                                      @NotNull final String options) {
        updateOptionsInOut.setRenameLoadSpectrumToLoadCollective(options.contains("R"));
    }

    private void setForceCalculation(@NotNull final HIUpdateOptions updateOptionsInOut,
                                     @NotNull final String options) {
        updateOptionsInOut.setForceCalculationIgnoringQueue(options.contains("Q"));
    }

    private void setRecalculateAllVehicles(@NotNull final HIUpdateOptions updateOptionsInOut,
                                           @NotNull final String options) {
        updateOptionsInOut.setRecalculateAllVehicles(options.contains("A"));
    }

    private void setLoadSpectraLimitation(@NotNull final HIUpdateOptions updateOptionsInOut,
                                          @NotNull final String limitationAsString) {
        updateOptionsInOut.setLimitVehicleTwinCount(limitationAsString.length() > 0);
        if(updateOptionsInOut.isLimitVehicleTwinCount()) {
            updateOptionsInOut.setMaxVehicleTwins(Integer.parseInt(limitationAsString));
        }
    }
}
