package com.practice.discoveryEvents.events;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.practice.discoveryEvents.util.LocationDTO;
import com.practice.discoveryEvents.util.StateActionUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Valid
public class UpdateEventUserRequest {

    @Size(min = 20, max = 2000, message = "Annotation must be between 20 and 2000 characters")
    String annotation;

    @Positive(message = "Category ID is must be a positive number")
    Integer category;

    @Size(min = 20, max = 7000, message = "Description must be between 20 and 7000 characters")
    String description;

    @Future(message = "Event date should be in the future")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    @Valid
    LocationDTO location;

    Boolean paid;

    @PositiveOrZero
    Integer participantLimit;

    Boolean requestModeration;

    StateActionUser stateAction;

    @Size(min = 3, max = 120, message = "Title must be between 3 and 120 characters")
    String title;

}
