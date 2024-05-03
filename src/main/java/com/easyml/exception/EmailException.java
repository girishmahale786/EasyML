package com.easyml.exception;

public class EmailException extends Exception {
    public EmailException() {
        super("Error sending email.");
    }

    public EmailException(String message) {
        super(message);
    }
}
