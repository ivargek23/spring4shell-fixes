package com.example.spring4shell.exception;

public class TokenReuseException extends RuntimeException {
    public TokenReuseException(String message) {
        super(message);
    }
}
