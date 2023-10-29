package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
public class Booking {
    long id;
    LocalDate startTime;
    LocalDate endTime;
    Item item;
    User booker;
    Status status;
}
