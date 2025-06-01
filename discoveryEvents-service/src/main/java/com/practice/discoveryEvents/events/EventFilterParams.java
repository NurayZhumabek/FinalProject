package com.practice.discoveryEvents.events;

import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFilterParams {

    String text;
    List<Integer> categoryIds;
    Boolean paid;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime rangeStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime rangeEnd;

    Boolean onlyAvailable;
    EventSortType sortBy;

    @Min(0)
    Integer from = 0;

    @Min(1)
    Integer size = 10;


}