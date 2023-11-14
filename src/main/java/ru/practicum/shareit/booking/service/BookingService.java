package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.OutputBookingDto;

import java.util.List;


public interface BookingService {

    OutputBookingDto addBooking(BookingDto bookingDto, long userId);

    OutputBookingDto approveBooking(long userId, long bookingId, Boolean approved);

    OutputBookingDto getBookingById(long userId, long bookingId);

    List<OutputBookingDto> getBookingsByBookerId(long userId, String state);

    List<OutputBookingDto> getBookingsForItemsByOwnerId(long userId, String state);
}