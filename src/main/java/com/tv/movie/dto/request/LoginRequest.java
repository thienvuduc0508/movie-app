package com.tv.movie.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "INVALID_EMAIL")
    @Email(message = "INVALID_EMAIL")
    private String email;

    @NotBlank(message = "REQUIRED_PASSWORD")
    @Size(min = 6, message = "INVALID_PASSWORD")
    private String password;

}
