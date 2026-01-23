package com.tv.movie.service;

import com.tv.movie.dto.request.LoginRequest;
import com.tv.movie.dto.request.UserRequest;
import com.tv.movie.dto.response.EmailValidationResponse;
import com.tv.movie.dto.response.LoginResponse;
import com.tv.movie.dto.response.MessageResponse;

public interface AuthService {
    MessageResponse registerUser(UserRequest userRequest);

    LoginResponse login(LoginRequest loginRequest);

    MessageResponse verificationEmail(String token);

    MessageResponse resendVerificationEmail( String email);

    EmailValidationResponse validateEmail(String email);

    MessageResponse forgotPassword(String email);

    MessageResponse resetPassword(String token, String newPassword);

    MessageResponse changePassword(String email, String currentPassword, String newPassword);

    LoginResponse currentUser(String email);
}
