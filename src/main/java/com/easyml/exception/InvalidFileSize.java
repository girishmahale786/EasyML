package com.easyml.exception;

public class InvalidFileSize extends Exception {
    public InvalidFileSize() {
        super("CSV must have more than 10 rows and 2 columns");
    }

    public InvalidFileSize(String message) {
        super(message);
    }
}