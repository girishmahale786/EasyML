package com.easyml.exception;

public class PermissionDenied extends Exception {

    private String path;

    public PermissionDenied() {
        super("You do not have the permission to perform this action.");
    }

    public PermissionDenied(String message) {
        super(message);
    }

    public String getPath() {
        return path;
    }

    public PermissionDenied setPath(String path) {
        this.path = path;
        return new PermissionDenied();
    }
}
