package com.easyml.exception;

public class UserNotFound extends Exception {
    public UserNotFound() {
        super("The account associated with the provided email was not found in our system.");
    }

    public UserNotFound(String message) {
        super(message);
    }
}
