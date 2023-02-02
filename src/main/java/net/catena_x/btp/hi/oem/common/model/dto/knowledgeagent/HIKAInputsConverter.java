package net.catena_x.btp.hi.oem.common.model.dto.knowledgeagent;

import net.catena_x.btp.hi.oem.common.model.dao.knowledgeagent.*;
import net.catena_x.btp.libraries.bamm.custom.classifiedloadspectrum.items.LoadSpectrumType;
import net.catena_x.btp.libraries.util.database.converter.DAOConverter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Stream;

@Component
public final class HIKAInputsConverter extends DAOConverter<HIKAInputsDAO, HIKAInputs> {
    protected HIKAInputs toDTOSourceExists(@NotNull final HIKAInputsDAO source) {
        final HIKAInputs inputs = new HIKAInputs();

        int count = getCount(source);
        inputs.setRequests(new ArrayList<>(count));
        inputs.setRequestId("CONVERTED");
        inputs.setMaxSyncCounter(0L);
        inputs.setCalculationTimestamp(Instant.now());

        if(count > 0) {
            for (final HIKAInputBindingDAO binding: source.getResults().getBindings()) {
                inputs.getRequests().add(new HIKAInput(getVan(binding), getAdaptionValues(binding)));
            }
        }

        return inputs;
    }

    protected HIKAInputsDAO toDAOSourceExists(@NotNull final HIKAInputs source) {
        final HIKAInputsDAO inputs = new HIKAInputsDAO();

        inputs.setHead(new HIKAHeaderDAO());
        inputs.getHead().setVars(new ArrayList<>());
        inputs.getHead().getVars().add("van");
        inputs.getHead().getVars().add("aggregate");
        inputs.getHead().getVars().add("healthType");
        inputs.getHead().getVars().add("adaptionValues");

        int count = getCount(source);

        inputs.setResults(new HIKAResultsDAO<>());
        inputs.getResults().setBindings(new ArrayList<>(count));

        if(count > 0) {
            for (final HIKAInput sourceinput: source.getRequests()) {
                addBinding(inputs, sourceinput.getVan(), "Differential Gear",
                        "Clutch", sourceinput.getAdaptionValues());
            }
        }

        return inputs;
    }

    private void addBinding(@NotNull final HIKAInputsDAO inputs, @NotNull final String van,
                            @NotNull final String aggregate, @NotNull final String healthType,
                            @NotNull final double[] adaptionValues) {
        inputs.getResults().getBindings().add(new HIKAInputBindingDAO(
                new HIKAVariableDAO("literal", van, null),
                new HIKAVariableDAO("literal", aggregate, null),
                new HIKAVariableDAO("literal", healthType, null),
                new HIKAVariableDAO("literal", convertToJson(adaptionValues), null)));
    }

    private int getCount(@NotNull final HIKAInputsDAO source) {
        if(source.getResults() == null) {
            return 0;
        }

        if(source.getResults().getBindings() == null) {
            return 0;
        }

        return source.getResults().getBindings().size();
    }

    private int getCount(@NotNull final HIKAInputs source) {
        if(source.getRequests() == null) {
            return 0;
        }

        return source.getRequests().size();
    }

    private String getVan(@NotNull final HIKAInputBindingDAO binding) {
        if(binding.getVan() == null) {
            return null;
        }

        return binding.getVan().getValue();
    }

    private double[] getAdaptionValues(@NotNull final HIKAInputBindingDAO binding) {
        if(binding.getAdaptionValues() == null) {
            return null;
        }

        if(binding.getAdaptionValues().getValue() == null) {
            return null;
        }

        return convertFromJson(binding.getAdaptionValues().getValue());
    }

    private double[] convertFromJson(@NotNull final String values) {
        return Stream.of(values.replace("[", "").replace("]", "")
                .replace(" ", "").split(",")).mapToDouble (Double::parseDouble).toArray();
    }

    private String convertToJson(@NotNull final double[] values) {
        if(values == null) {
            return null;
        }

        if(values.length != 4) {
            return "ERROR in value length, must be 4!";
        }

        return String.format(Locale.US, "[%f, %f, %f, %f]", values[0], values[1], values[2], values[3]);
    }
}