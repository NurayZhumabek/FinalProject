package com.practice.discoveryEvents.requests;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestStatusUpdateResult {

    List<ParticipationRequestDTO> confirmedRequests;
    List<ParticipationRequestDTO> rejectedRequests;
}
