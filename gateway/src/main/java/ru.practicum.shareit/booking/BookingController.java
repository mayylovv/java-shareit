package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.UnsupportedStatusException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader(HEADER_USER_ID) Long userId,
                                             @RequestBody @Valid BookingDto bookingDto) {
        return bookingClient.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(HEADER_USER_ID) Long userId, @PathVariable Long bookingId,
                                                 @RequestParam Boolean approved) {
        return bookingClient.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(HEADER_USER_ID) Long userId, @PathVariable Long bookingId) {
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByBookerId(@RequestHeader(HEADER_USER_ID) Long userId,
                                                        @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                        @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam).orElseThrow(() -> new UnsupportedStatusException("Unknown state: " + stateParam));
        return bookingClient.getBookingsByBookerId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsForItemsByOwnerId(@RequestHeader(HEADER_USER_ID) Long userId,
                                                               @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                               @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                               @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam).orElseThrow(() -> new UnsupportedStatusException("Unknown state: " + stateParam));
        return bookingClient.getBookingsForItemsByOwnerId(userId, state, from, size);
    }
}