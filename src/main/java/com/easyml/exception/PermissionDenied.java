package com.easyml.exception;

public class PermissionDenied extends Exception {

    public PermissionDenied() {
        super("You do not have the permission to perform this action.");
    }

    public PermissionDenied(String message) {
        super(message);
    }
}
