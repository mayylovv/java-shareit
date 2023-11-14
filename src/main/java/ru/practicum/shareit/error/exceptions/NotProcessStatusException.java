package ru.practicum.shareit.error.exceptions;

public class NotProcessStatusException extends RuntimeException {
    public NotProcessStatusException(String message) {
        super(message);
    }
}