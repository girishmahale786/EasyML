package com.easyml.exception;

public class EncryptionException extends Exception {
    public EncryptionException() {
        super("Error encrypting the message.");
    }

    public EncryptionException(String message) {
        super(message);
    }
}
