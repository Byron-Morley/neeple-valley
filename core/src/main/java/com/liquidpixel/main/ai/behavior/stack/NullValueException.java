package com.liquidpixel.main.ai.behavior.stack;

public class NullValueException extends RuntimeException {
    public NullValueException() {
        super("The requested type field is null");
    }
}
