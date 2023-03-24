package ru.practicum.server.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
public class NewUserRequest {
    @Pattern(regexp = "^[^ ].*[^ ]$", message = "invalid name")
    @Size(max = 255)
    @NotNull(message = "Field: name. Error: must not be blank. Value: null")
    private String name;
    @Email(message = "invalid email")
    @NotNull
    private String email;
}
