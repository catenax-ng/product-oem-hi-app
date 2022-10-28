package net.catena_x.btp.hi.supplier.data.input;

public record ClassifiedLoadCollective(
        String targetComponentID,
        Metadata metadata,
        Header header,
        Body body
) {}
