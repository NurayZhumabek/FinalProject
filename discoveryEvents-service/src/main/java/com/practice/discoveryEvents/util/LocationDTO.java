package com.practice.discoveryEvents.util;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocationDTO {

    @NotNull(message = "Latitude is required")
    Float latitude;

    @NotNull(message = "Longitude is required")
    Float longitude;
}
