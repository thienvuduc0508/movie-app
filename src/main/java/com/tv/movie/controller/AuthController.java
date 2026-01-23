package com.tv.movie.controller;

import com.tv.movie.dto.request.*;
import com.tv.movie.dto.response.ApiResponse;
import com.tv.movie.dto.response.EmailValidationResponse;
import com.tv.movie.dto.response.LoginResponse;
import com.tv.movie.dto.response.MessageResponse;
import com.tv.movie.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ApiResponse<MessageResponse> registerUser(@Valid @RequestBody UserRequest userRequest) {
        return ApiResponse.<MessageResponse>builder().result(authService.registerUser(userRequest)).build();
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ApiResponse.<LoginResponse>builder()
                .result(authService.login(loginRequest))
                .build();
    }

    @GetMapping("/validate-email")
    public ApiResponse<EmailValidationResponse> validateEmail(@RequestParam String email){
        return ApiResponse.<EmailValidationResponse>builder()
                .result(authService.validateEmail(email)).build();
    }

    @GetMapping("/verify-email")
    public ApiResponse<MessageResponse> verificationEmail(@RequestParam String token) {
        return ApiResponse.<MessageResponse>builder()
                .result(authService.verificationEmail(token))
                .build();
    }

    @PostMapping("/resend-verification")
    public ApiResponse<MessageResponse> resendVerificationEmail(@Valid @RequestBody EmailRequest emailRequest) {
      return ApiResponse.<MessageResponse>builder()
              .result(authService.resendVerificationEmail(emailRequest.getEmail()))
              .build();
    }

    @PostMapping("/forgot-password")
    public ApiResponse<MessageResponse> forgotPassword(@Valid @RequestBody EmailRequest emailRequest) {
        return ApiResponse.<MessageResponse>builder().result(authService.forgotPassword(emailRequest.getEmail())).build();
    }
    @PostMapping("/reset-password")
    public ApiResponse<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest){
        return ApiResponse.<MessageResponse>builder()
                .result(authService.resetPassword(resetPasswordRequest.getToken(), resetPasswordRequest.getNewPassword()))
                .build();
    }

    @PostMapping("/change-password")
    public ApiResponse<MessageResponse> changePassword(Authentication authentication, @RequestBody ChangePasswordRequest changePasswordRequest){
     return ApiResponse.<MessageResponse>builder()
             .result(authService.changePassword(authentication.getName(), changePasswordRequest.getCurrentPassword(), changePasswordRequest.getNewPassword()))
             .build();
    }

    @GetMapping("/current-user")
    public ApiResponse<LoginResponse> currentUser(Authentication authentication){
        String email = authentication.getName();
        return ApiResponse.<LoginResponse>builder().result(authService.currentUser(email)).build();
    }
}
