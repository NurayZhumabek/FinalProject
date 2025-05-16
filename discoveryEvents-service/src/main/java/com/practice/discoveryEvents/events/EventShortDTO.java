package com.practice.discoveryEvents.events;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.practice.discoveryEvents.categories.CategoryDTO;
import com.practice.discoveryEvents.users.UserShortDTO;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDTO {

    @NotBlank(message = "Annotation must not be empty")
    @Size(min = 20, max = 2000, message = "Annotation must be between 20 and 2000 characters")
    String annotation;

    @NotNull
    CategoryDTO category;

    Integer confirmedResults;

    @NotNull
    @Future(message = "Event date should be in the future")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    Integer id;

    @NotNull
    UserShortDTO initiator;

    @NotNull
    Boolean paid;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 120, message = "Title must be between 3 and 120 characters")
    String title;

    Integer views;

}
