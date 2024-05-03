package com.easyml.exception;

public class InvalidOtp extends Exception {

    public InvalidOtp() {
        super("The one-time password (OTP) you entered is incorrect or has expired.");
    }

    public InvalidOtp(String message) {
        super(message);
    }
}
