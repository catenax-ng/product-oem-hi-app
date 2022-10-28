package net.catena_x.btp.hi.supplier.data.input;

import java.util.Date;

public record Status(
        Date date,
        double operatingTime,
        long mileage
) {}
