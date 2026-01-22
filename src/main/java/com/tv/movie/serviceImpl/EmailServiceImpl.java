package com.tv.movie.serviceImpl;

import com.tv.movie.exception.AppException;
import com.tv.movie.exception.ErrorCode;
import com.tv.movie.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;



    @Override
    public void sendVerificationEmail(String toEmail, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Verify your email");
            String verificationLink = frontendUrl + "/api/auth/verify-email?token=" + token;
            message.setText("Please click the link below to verify your email:\n" + verificationLink);
            mailSender.send(message);
        } catch (Exception ex) {
            log.error("Failed to send email to {}: {}", toEmail, ex.getMessage(), ex);
            throw new AppException(ErrorCode.FAILED_SEND_EMAIL);
        }
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Reset your password");
            String resetLink = frontendUrl + "/api/auth/reset-password?token=" + token;
            message.setText("Please click the link below to reset your password:\n" + resetLink);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage(), e);
            throw new AppException(ErrorCode.FAILED_SEND_EMAIL);
        }
    }
}
