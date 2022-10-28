package net.catena_x.btp.hi.supplier.data.input;

import java.time.Instant;

public record AdaptionValueList (
    String version,
    Instant timestamp,
    double mileage_km,
    long operatingtime_s,
    double[] values
) {}
