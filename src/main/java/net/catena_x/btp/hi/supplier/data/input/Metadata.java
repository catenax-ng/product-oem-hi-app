package net.catena_x.btp.hi.supplier.data.input;

public record Metadata(
        String projectDescription,
        String componentDescription,
        String routeDescription,
        Status status
) {}
