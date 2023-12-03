package ru.practicum.shareit.error.exception;

public class NotFoundEmailException extends RuntimeException {

    public NotFoundEmailException(String message) {
        super(message);
    }
}