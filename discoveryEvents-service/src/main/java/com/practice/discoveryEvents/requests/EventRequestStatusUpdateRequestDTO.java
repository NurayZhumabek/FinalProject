package com.practice.discoveryEvents.requests;

import com.practice.discoveryEvents.util.RequestStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestStatusUpdateRequestDTO {

    List<Integer> requestIds;
    RequestStatus status;
}
