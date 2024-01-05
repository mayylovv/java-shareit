package ru.practicum.shareit.error.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(Class<?> entityClass, String s) {
        super("Entity " + entityClass.getSimpleName() + " not found. " + s);
    }

    public NotFoundException(String s) {
        super(s);
    }
}