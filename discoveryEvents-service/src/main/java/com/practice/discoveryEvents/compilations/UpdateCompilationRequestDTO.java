package com.practice.discoveryEvents.compilations;


import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCompilationRequestDTO {

    List<Integer> eventIds;
    Boolean pinned;

    @Size(min = 1,max = 50,message = "Title must be between 1 and 50 characters")
    String title;
}
