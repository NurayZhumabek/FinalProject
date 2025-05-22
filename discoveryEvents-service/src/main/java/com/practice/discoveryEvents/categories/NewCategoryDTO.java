package com.practice.discoveryEvents.categories;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewCategoryDTO {

    @NotBlank(message = "Category name is required")
    @Size(min =1,max = 50,message = "Category name must be between 1 and 50 characters")
    private String name;

}