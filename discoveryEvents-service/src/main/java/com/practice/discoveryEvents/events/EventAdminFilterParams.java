package com.practice.discoveryEvents.events;

import com.practice.discoveryEvents.util.Status;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class EventAdminFilterParams {
    List<Integer> userIds;
    List<Status> statuses;
    List<Integer> categoryIds;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime rangeStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime rangeEnd;

    @Min(0)
    Integer from = 0;

    @Min(1)
    Integer size = 10;


}
