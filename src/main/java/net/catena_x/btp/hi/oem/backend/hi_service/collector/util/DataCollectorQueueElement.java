package net.catena_x.btp.hi.oem.backend.hi_service.collector.util;

public record DataCollectorQueueElement(
        DataCollectorQueueState queueState,
        String options
) {}