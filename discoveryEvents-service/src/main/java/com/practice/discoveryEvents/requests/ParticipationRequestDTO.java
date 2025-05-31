package com.practice.discoveryEvents.requests;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.practice.discoveryEvents.util.RequestStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequestDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime created;

    Integer event;
    Integer requester;
    RequestStatus status;
    Integer id;
}
