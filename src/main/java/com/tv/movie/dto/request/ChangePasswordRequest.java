package com.tv.movie.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank(message = "REQUIRED_PASSWORD")
    private String currentPassword;
    @NotBlank(message = "REQUIRED_PASSWORD")
    private String newPassword;
}
