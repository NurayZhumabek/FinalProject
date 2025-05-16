package com.practice.discoveryEvents.events;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.practice.discoveryEvents.categories.CategoryDTO;
import com.practice.discoveryEvents.users.UserShortDTO;
import com.practice.discoveryEvents.util.LocationDTO;
import com.practice.discoveryEvents.util.Status;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDTO {

    String annotation;
    CategoryDTO category;
    Integer confirmedRequests;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdOn;

    String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    Integer id;
    UserShortDTO initiator;
    LocationDTO location;
    Boolean paid;
    Integer participantLimit;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime publishedOn;

    Boolean requestModeration;
    Status status;
    String title;
    Integer views;


}
