package com.tv.movie.service;

import com.tv.movie.dto.request.LoginRequest;
import com.tv.movie.dto.request.UserRequest;
import com.tv.movie.dto.response.LoginResponse;
import com.tv.movie.dto.response.MessageResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public interface AuthService {
    MessageResponse registerUser(UserRequest userRequest);

    LoginResponse login(LoginRequest loginRequest);

    MessageResponse verificationEmail(String token);

    MessageResponse resendVerificationEmail( String email);
}
