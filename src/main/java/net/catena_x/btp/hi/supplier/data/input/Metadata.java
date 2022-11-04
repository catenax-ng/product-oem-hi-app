package net.catena_x.btp.hi.supplier.data.input;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record Metadata(
        String projectDescription,
        String componentDescription,
        String routeDescription,
        Status status
) {}
