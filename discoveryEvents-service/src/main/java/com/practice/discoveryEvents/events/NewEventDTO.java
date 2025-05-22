package com.practice.discoveryEvents.events;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.practice.discoveryEvents.util.LocationDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
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
public class NewEventDTO {

    @NotBlank(message = "Annotation must not be empty")
    @Size(min = 20, max = 2000, message = "Annotation must be between 20 and 2000 characters")
    String annotation;

    @NotNull(message = "Category ID is required")
    Integer category;

    @NotBlank(message = "Description is required")
    @Size(min = 20, max = 7000, message = "Description must be between 20 and 7000 characters")
    String description;

    @NotNull(message = "Event date is required")
    @Future(message = "Event date should be in the future")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    @NotNull
    LocationDTO location;

    Boolean paid = false;

    @PositiveOrZero
    Integer participantLimit = 0;

    Boolean requestModeration =true;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 120, message = "Title must be between 3 and 120 characters")
    String title;


}
