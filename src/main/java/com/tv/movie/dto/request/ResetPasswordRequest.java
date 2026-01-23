package com.tv.movie.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank
    private String token;

    @NotBlank(message = "REQUIRED_PASSWORD")
    @Size(min = 6, message = "INVALID_PASSWORD")
    private String newPassword;


}
