package com.practice.discoveryEvents.users;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewUserRequest {

    @NotBlank(message = "Name must not be empty")
    String name;

    @NotBlank(message = "Email must not be empty")
    @Email(message = "Email format is invalid")
    String email;
}
