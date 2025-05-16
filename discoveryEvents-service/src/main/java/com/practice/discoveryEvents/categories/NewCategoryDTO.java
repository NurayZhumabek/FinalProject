package com.practice.discoveryEvents.categories;


import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewCategoryDTO {

    @NotBlank(message = "Category name is required")
    String name;


}