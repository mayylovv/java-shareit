package ru.practicum.shareit.error.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String s) {
        super(s);
    }
}