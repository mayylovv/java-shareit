package ru.practicum.shareit.booking.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.error.exceptions.UnauthorizedAccessException;
import ru.practicum.shareit.error.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;


    @Override
    @Transactional
    public OutputBookingDto addBooking(BookingDto bookingDto, long userId) {
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format("Позиция с ID %d не найдена", bookingDto.getItemId())));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с ID %d не найден", userId)));
        Booking booking = BookingMapper.returnBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(user);
        if (item.getOwner().equals(user)) {
            throw new NotFoundException(String.format("Владелец с ID %d не может бронировать свой предмет", userId));
        }
        if (!item.getAvailable()) {
            throw new ValidationException(String.format("Позиция с ID %d уже забронирована", item.getId()));
        }
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new ValidationException("Начало не может быть после конца");
        }
        if (booking.getStart().isEqual(booking.getEnd())) {
            throw new ValidationException("Начало не может совпадать с концом");
        }
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public OutputBookingDto approveBooking(long userId, long bookingId, Boolean approved) {
        checkBooking(bookingId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронь не найдена"));
        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException(String.format("Только владелец с ID %d предмета может изменить статус брони", userId));
        }
        if (approved) {
            if (booking.getStatus().equals(Status.APPROVED)) {
                throw new ValidationException("Некорректный статус");
            }
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public OutputBookingDto getBookingById(long userId, long bookingId) {
        checkBooking(bookingId);
        Booking booking = bookingRepository.findById(bookingId).get();
        checkUser(userId);
        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new UnauthorizedAccessException("Получить информацию о бронировании может получить только владелец и арендатор");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OutputBookingDto> getBookingsByBookerId(long userId, String state) {
        checkUser(userId);
        List<Booking> bookings = null;
        BookingState bookingState = BookingState.getEnumValue(state);
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                break;
        }
        return BookingMapper.returnBookingDtoList(bookings);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OutputBookingDto> getBookingsForItemsByOwnerId(long userId, String state) {
        checkUser(userId);
        if (itemRepository.findByOwnerId(userId).isEmpty()) {
            throw new ValidationException("У пользователя нет позиций для брони");
        }
        List<Booking> bookings = null;
        BookingState bookingState = BookingState.getEnumValue(state);
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartAsc(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                break;
        }
        return BookingMapper.returnBookingDtoList(bookings);
    }

    private void checkUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с ID %d не найден", userId));
        }
    }

    private void checkBooking(long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new NotFoundException(String.format("Бронь с ID %d не найдена", bookingId));
        }
    }
}