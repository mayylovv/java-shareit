package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<OutputBookingDto> addBooking(@RequestHeader(HEADER_USER_ID) Long userId,
                                                       @RequestBody @Valid BookingDto bookingDto) {
        return ResponseEntity.ok(bookingService.addBooking(bookingDto, userId));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<OutputBookingDto> approve(@PathVariable Long bookingId, @RequestParam boolean approved,
                                                    @RequestHeader(HEADER_USER_ID) Long userId) {
        return ResponseEntity.ok(bookingService.approveBooking(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<OutputBookingDto> getBookingById(@PathVariable Long bookingId,
                                                           @RequestHeader(HEADER_USER_ID) Long userId) {
        return ResponseEntity.ok(bookingService.getBookingById(userId, bookingId));
    }

    @GetMapping
    public ResponseEntity<List<OutputBookingDto>> getBookingsByBookerId(@RequestHeader(HEADER_USER_ID) Long userId,
                                                                        @RequestParam(defaultValue = "ALL", required = false) String state,
                                                                        @RequestParam(defaultValue = "0", required = false) Integer from,
                                                                        @RequestParam(defaultValue = "10", required = false) Integer size) {
        return ResponseEntity.ok(bookingService.getBookingsByBookerId(userId, state, from, size));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<OutputBookingDto>> getBookingsForItemsByOwnerId(@RequestHeader(HEADER_USER_ID) Long userId,
                                                                               @RequestParam(defaultValue = "ALL", required = false) String state,
                                                                               @RequestParam(defaultValue = "0", required = false) Integer from,
                                                                               @RequestParam(defaultValue = "10", required = false) Integer size) {
        return ResponseEntity.ok(bookingService.getBookingsForItemsByOwnerId(userId, state, from, size));
    }
}