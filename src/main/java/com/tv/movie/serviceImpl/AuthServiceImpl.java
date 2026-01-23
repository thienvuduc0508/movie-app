package com.tv.movie.serviceImpl;

import com.tv.movie.dao.UserRepository;
import com.tv.movie.dto.request.LoginRequest;
import com.tv.movie.dto.request.UserRequest;
import com.tv.movie.dto.response.EmailValidationResponse;
import com.tv.movie.dto.response.LoginResponse;
import com.tv.movie.dto.response.MessageResponse;
import com.tv.movie.entity.User;
import com.tv.movie.exception.AppException;
import com.tv.movie.exception.ErrorCode;
import com.tv.movie.mapper.UserMapper;
import com.tv.movie.security.JwtUtil;
import com.tv.movie.service.AuthService;
import com.tv.movie.service.EmailService;
import com.tv.movie.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ServiceUtils serviceUtils;
    private final UserMapper userMapper;



    @Override
    public MessageResponse registerUser(UserRequest userRequest) {
        if(userRepository.existsByEmail(userRequest.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        User user = userMapper.toUser(userRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        emailService.sendVerificationEmail(user.getEmail(), user.getVerificationToken());

        return new MessageResponse("User registered successfully! Please check your email to verify your account.");
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .filter(u -> passwordEncoder.matches(loginRequest.getPassword(), u.getPassword()))
                .orElseThrow(() -> new AppException(ErrorCode.FAILED_TO_LOGIN));
        if(!user.isActive()) {
            throw new AppException(ErrorCode.DEACTIVE_USER);
        }
        if(!user.isEmailVerified()) {
            throw new AppException(ErrorCode.NOT_VERIFIED_USER);
        }

        final String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new LoginResponse(token, user.getEmail(), user.getFullName(), user.getRole().name());
    }

    @Override
    public MessageResponse verificationEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_VERIFICATION_TOKEN));

        if(user.getVerificationTokenExpiry() == null || user.getVerificationTokenExpiry().isBefore(Instant.now())) {
            throw new AppException(ErrorCode.EXPIRED_VERIFICATION_LINK);
        }

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);
        return new MessageResponse("Email verified successfully!");
    }

    @Override
    public MessageResponse resendVerificationEmail(String email) {
        User user = serviceUtils.getUserByEmail(email);
        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiry(Instant.now().plusSeconds(86400));
        userRepository.save(user);
        emailService.sendVerificationEmail(email, verificationToken);
        return new MessageResponse("Verification email sent successfully!");
    }

    @Override
    public EmailValidationResponse validateEmail(String email) {
        boolean exists = userRepository.existsByEmail(email);
        return new EmailValidationResponse(exists, !exists);
    }

    @Override
    public MessageResponse forgotPassword(String email) {
        User user = serviceUtils.getUserByEmail(email);
        String token = UUID.randomUUID().toString();
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiry(Instant.now().plusSeconds(3600));
        userRepository.save(user);
        emailService.sendPasswordResetEmail(email, token);
        return new MessageResponse("Password reset email sent successfully!");
    }

    @Override
    public MessageResponse resetPassword(String token, String newPassword) {
        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_PASSWORD_RESET_TOKEN));
        if(user.getPasswordResetTokenExpiry() == null || user.getPasswordResetTokenExpiry().isBefore(Instant.now())){
            throw new AppException(ErrorCode.INVALID_PASSWORD_RESET_TOKEN);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        userRepository.save(user);
        return new MessageResponse("Password reset successfully!");
    }

    @Override
    public MessageResponse changePassword(String email, String currentPassword, String newPassword) {
        User user = serviceUtils.getUserByEmail(email);
        if(!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CURRENT_PASSWORD);
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return new MessageResponse("Password changed successfully!");
    }

    @Override
    public LoginResponse currentUser(String email) {
        User user = serviceUtils.getUserByEmail(email);
        return new LoginResponse(null, user.getEmail(), user.getFullName(), user.getRole().name());
    }
}
