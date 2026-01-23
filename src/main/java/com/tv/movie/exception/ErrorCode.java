package com.tv.movie.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    REQUIRED_PASSWORD(1004, "Password is required", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_EMAIL(1008, "Invalid email", HttpStatus.BAD_REQUEST),
    VIDEO_NOT_FOUND(1009, "Video not found", HttpStatus.NOT_FOUND),
    FAILED_SEND_EMAIL(1010, "Failed to send email", HttpStatus.INTERNAL_SERVER_ERROR),
    FAILED_TO_LOGIN(1011, "Failed to login", HttpStatus.BAD_REQUEST),
    DEACTIVE_USER(1012, "Your account has been deactivated", HttpStatus.BAD_REQUEST),
    NOT_VERIFIED_USER(1012, "Your account has not been verified", HttpStatus.BAD_REQUEST),
    INVALID_VERIFICATION_TOKEN(1013, "Invalid verification token", HttpStatus.BAD_REQUEST),
    EXPIRED_VERIFICATION_LINK(1013, "Expired verification link", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD_RESET_TOKEN(1014, "Invalid or expired reset token", HttpStatus.BAD_REQUEST),
    INVALID_CURRENT_PASSWORD(1014, "Invalid current password", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_MATCH(1014, "Password not match", HttpStatus.BAD_REQUEST),

    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
