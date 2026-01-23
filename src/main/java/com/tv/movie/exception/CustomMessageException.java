package com.tv.movie.exception;

public class CustomMessageException extends RuntimeException {
    public CustomMessageException(String message) {
        super(message);
    }
}
