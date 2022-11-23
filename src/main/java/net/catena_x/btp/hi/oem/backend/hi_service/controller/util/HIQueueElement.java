package net.catena_x.btp.hi.oem.backend.hi_service.controller.util;

public record HIQueueElement(
        HIQueueState queueState,
        String options
) {}