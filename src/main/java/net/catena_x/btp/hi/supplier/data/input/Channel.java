package net.catena_x.btp.hi.supplier.data.input;

public record Channel(
        String channelName,
        String unit,
        double lowerLimit,
        double upperLimit,
        int numberOfBins
) {}
