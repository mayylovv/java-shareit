package ru.practicum.shareit.error.exceptions;

public class NotFoundEmailException extends RuntimeException {
    public NotFoundEmailException(String message) {
        super(message);
    }
}