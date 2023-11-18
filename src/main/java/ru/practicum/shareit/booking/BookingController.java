package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;


@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public OutputBookingDto addBooking(@RequestHeader(HEADER_USER_ID) Long userId,
                                       @RequestBody @Valid BookingDto bookingDto) {
        return bookingService.addBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public OutputBookingDto approve(@PathVariable Long bookingId, @RequestParam boolean approved,
                                    @RequestHeader(HEADER_USER_ID) Long userId) {
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public OutputBookingDto getBookingById(@PathVariable Long bookingId, @RequestHeader(HEADER_USER_ID) Long userId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<OutputBookingDto> getBookingsByBookerId(@RequestHeader(HEADER_USER_ID) Long userId,
                                                        @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.getBookingsByBookerId(userId, state);
    }

    @GetMapping("/owner")
    public List<OutputBookingDto> getBookingsForItemsByOwnerId(@RequestHeader(HEADER_USER_ID) Long userId,
                                                               @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.getBookingsForItemsByOwnerId(userId, state);
    }
}