package net.catena_x.btp.hi.supplier.data.input;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record Header(
        String countingMethod,      // TODO enum?
        List<Channel> channels,
        String countingValue,
        String countingUnit
) {}
