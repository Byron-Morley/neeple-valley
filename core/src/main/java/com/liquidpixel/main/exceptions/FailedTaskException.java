package com.liquidpixel.main.exceptions;

public class FailedTaskException extends RuntimeException {

    public FailedTaskException() {
    }

    public FailedTaskException(String message) {
        super(message);
    }
}
