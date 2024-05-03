package com.easyml.exception;

public class InvalidCredentials extends Exception {

    public InvalidCredentials() {
        super("The password you entered is incorrect.");
    }

    public InvalidCredentials(String message) {
        super(message);
    }
}
