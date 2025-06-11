package com.practice.discoveryEvents.stats;


import lombok.*;
import lombok.experimental.FieldDefaults;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EndpointHit {
    String app;
    String uri;
    String ip;
    String timestamp;
}
