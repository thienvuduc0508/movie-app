package com.tv.movie.controller;

import com.tv.movie.dto.request.EmailRequest;
import com.tv.movie.dto.request.LoginRequest;
import com.tv.movie.dto.request.UserRequest;
import com.tv.movie.dto.response.ApiResponse;
import com.tv.movie.dto.response.LoginResponse;
import com.tv.movie.dto.response.MessageResponse;
import com.tv.movie.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
}
