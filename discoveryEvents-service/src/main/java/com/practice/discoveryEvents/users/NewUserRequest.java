package com.practice.discoveryEvents.users;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewUserRequest {

    @NotBlank(message = "Name must not be empty")
    @Size(min = 2,max = 250,message = "User name must be between 1 and 50 characters")
    String name;

    @NotBlank(message = "Email must not be empty")
    @Email(message = "Email format is invalid")
    @Size(min = 6,max = 254,message = "Email name must be between 1 and 50 characters")
    String email;
}
