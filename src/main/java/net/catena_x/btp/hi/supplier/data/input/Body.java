package net.catena_x.btp.hi.supplier.data.input;

import java.util.List;

public record Body(
        List<HIClass> classes,
        List<HICounts> counts
) {}
