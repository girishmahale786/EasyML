package com.easyml.exception;

public class InvalidFileType extends Exception {
    public InvalidFileType() {
        super("Invalid file format, expected CSV");
    }

    public InvalidFileType(String message) {
        super(message);
    }
}