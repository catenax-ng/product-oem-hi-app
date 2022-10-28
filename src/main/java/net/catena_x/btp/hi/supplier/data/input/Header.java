package net.catena_x.btp.hi.supplier.data.input;

import java.util.List;

public record Header(
        String countingMethod,      // TODO enum?
        List<Channel> channels,
        String countingValue,
        String countingUnit
) {}
