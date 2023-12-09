package ru.practicum.shareit.error;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.shareit.error.exception.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ErrorHandlerTest {

    @Mock
    private ValidationException validationException;

    @Mock
    private NotProcessStatusException notProcessStatusException;

    @Mock
    private NotFoundEmailException notFoundEmailException;

    @Mock
    private NotFoundException notFoundException;

    @Mock
    private UnauthorizedAccessException unauthorizedAccessException;

    @InjectMocks
    private ErrorHandler errorHandler;

    @Test
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    void handleValidationException() {
        when(validationException.getMessage()).thenReturn("Validation error message");
        ErrorResponse response = errorHandler.handleValidationException(validationException);
        assertEquals("Validation error message", response.getError());
    }

    @Test
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    void handleUnsupportedStatusException() {
        when(notProcessStatusException.getMessage()).thenReturn("Unsupported status error message");
        ErrorResponse response = errorHandler.handleUnsupportedStatusException(notProcessStatusException);
        assertEquals("Unsupported status error message", response.getError());
    }

    @Test
    @ResponseStatus(HttpStatus.CONFLICT)
    void handleEmailExistException() {
        when(notFoundEmailException.getMessage()).thenReturn("Email not found error message");
        ErrorResponse response = errorHandler.handleEmailExistException(notFoundEmailException);
        assertEquals("Email not found error message", response.getError());
    }

    @Test
    @ResponseStatus(HttpStatus.NOT_FOUND)
    void handleNotFoundException() {
        when(notFoundException.getMessage()).thenReturn("Not found error message");
        ErrorResponse response = errorHandler.handleNotFoundException(notFoundException);
        assertEquals("Not found error message", response.getError());
    }

    @Test
    @ResponseStatus(HttpStatus.NOT_FOUND)
    void handleUnauthorizedAccessException() {
        when(unauthorizedAccessException.getMessage()).thenReturn("Unauthorized access error message");
        ErrorResponse response = errorHandler.handleUnauthorizedAccessException(unauthorizedAccessException);
        assertEquals("Unauthorized access error message", response.getError());
    }
}