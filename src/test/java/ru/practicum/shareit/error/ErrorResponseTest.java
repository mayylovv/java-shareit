package ru.practicum.shareit.error;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ErrorResponseTest {
    ErrorResponse errorResponse;

    String error = "textError";

    @Test
    void testErrorResponse() {
        errorResponse = new ErrorResponse(error);
        assertEquals(error, errorResponse.getError());
    }
}