package com.practice.discoveryEvents.compilations;


import com.practice.discoveryEvents.events.EventShortDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationDTO {

    List<EventShortDTO> events;
    Integer id;
    Boolean pinned;
    String title;


}
