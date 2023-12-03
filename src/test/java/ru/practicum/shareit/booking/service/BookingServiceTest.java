package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private BookingRepository bookingRepository;

    private User user1;
    private User user2;
    private Item item;
    private ItemDto itemDto;
    private Booking booking1;
    private Booking booking2;
    private BookingDto bookingDto;

    @BeforeEach
    void beforeEach() {
        user1 = User.builder()
                .id(1L)
                .name("vasiliy")
                .email("vasiliy@yandex.ru")
                .build();
        user2 = User.builder()
                .id(2L)
                .name("ivan")
                .email("ivan@yandex.ru")
                .build();
        item = Item.builder()
                .id(1L)
                .name("ringOfForce")
                .description("from movie ringOfForce")
                .available(true)
                .owner(user1)
                .build();
        itemDto = ItemMapper.returnItemDto(item);
        booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(item)
                .booker(user1)
                .status(Status.APPROVED)
                .build();
        booking2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(item)
                .booker(user1)
                .status(Status.WAITING)
                .build();
        bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2022, 4, 8, 3, 2))
                .end(LocalDateTime.of(2022, 5, 9, 3, 2))
                .status(Status.APPROVED)
                .build();
    }

    @Test
    void testAddBooking() {
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking1);
        OutputBookingDto bookingOutDtoTest = bookingService.addBooking(bookingDto, anyLong());

        assertEquals(bookingOutDtoTest.getItem(), itemDto);
        assertEquals(bookingOutDtoTest.getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.getBooker(), UserMapper.returnUserDto(user2));

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testAddBookingWrongOwner() {
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(bookingDto, anyLong()));
    }

    @Test
    void testAddBookingItemBooked() {

        item.setAvailable(false);
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));

        assertThrows(ValidationException.class, () -> bookingService.addBooking(bookingDto, anyLong()));
    }

    @Test
    void testAddBookingNotValidDateEnd() {
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        bookingDto.setEnd(LocalDateTime.of(2020, 11, 11, 11, 11));

        assertThrows(ValidationException.class, () -> bookingService.addBooking(bookingDto, anyLong()));
    }

    @Test
    void testAddBookingNotValidDateStart() {
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        bookingDto.setStart(LocalDateTime.of(2024, 11, 11, 11, 11));

        assertThrows(ValidationException.class, () -> bookingService.addBooking(bookingDto, anyLong()));
    }

    @Test
    void testApproveBooking() {
        OutputBookingDto bookingOutDtoTest;
        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking2));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking2);
        bookingOutDtoTest = bookingService.approveBooking(user1.getId(), item.getId(), true);

        assertEquals(bookingOutDtoTest.getStatus(), Status.APPROVED);

        bookingOutDtoTest = bookingService.approveBooking(user1.getId(), item.getId(), false);

        assertEquals(bookingOutDtoTest.getStatus(), Status.REJECTED);

        verify(bookingRepository, times(2)).save(any(Booking.class));
    }

    @Test
    void testApproveBookingWrongUser() {
        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking2));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking2);

        assertThrows(NotFoundException.class, () -> bookingService.approveBooking(user2.getId(), item.getId(), true));
    }

    @Test
    void testApproveBookingNotValidStatus() {
        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking1);

        assertThrows(ValidationException.class, () -> bookingService.approveBooking(user1.getId(), item.getId(), true));
    }

    @Test
    void testGetBookingById() {
        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        OutputBookingDto bookingOutDtoTest = bookingService.getBookingById(user1.getId(), booking1.getId());

        assertEquals(bookingOutDtoTest.getItem(), itemDto);
        assertEquals(bookingOutDtoTest.getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.getBooker(), UserMapper.returnUserDto(user1));

    }

    @Test
    void testGetBookingByErrorId() {
        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));
        when(userRepository.existsById(anyLong())).thenReturn(true);

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(2L, booking1.getId()));
    }

    @Test
    void testGetAllBookingsByBookerId() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));
        String state = "ALL";
        List<OutputBookingDto> bookingOutDtoTest = bookingService.getBookingsByBookerId(user1.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.returnUserDto(user1));

        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));
        state = "CURRENT";
        bookingOutDtoTest = bookingService.getBookingsByBookerId(user1.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.returnUserDto(user1));

        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));
        state = "PAST";
        bookingOutDtoTest = bookingService.getBookingsByBookerId(user1.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.returnUserDto(user1));

        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));
        state = "FUTURE";
        bookingOutDtoTest = bookingService.getBookingsByBookerId(user1.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.returnUserDto(user1));

        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(Status.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));
        state = "WAITING";
        bookingOutDtoTest = bookingService.getBookingsByBookerId(user1.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.returnUserDto(user1));

        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(Status.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));
        state = "REJECTED";
        bookingOutDtoTest = bookingService.getBookingsByBookerId(user1.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.returnUserDto(user1));
    }

    @Test
    void testGetAllBookingsForAllItemsByOwnerId() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findByOwnerId(anyLong())).thenReturn(List.of(item));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(anyLong(), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));
        String state = "ALL";
        List<OutputBookingDto> bookingOutDtoTest = bookingService.getBookingsForItemsByOwnerId(user1.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.returnUserDto(user1));

        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartAsc(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));
        state = "CURRENT";
        bookingOutDtoTest = bookingService.getBookingsForItemsByOwnerId(user1.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.returnUserDto(user1));

        when(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));
        state = "PAST";
        bookingOutDtoTest = bookingService.getBookingsForItemsByOwnerId(user1.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.returnUserDto(user1));

        when(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));
        state = "FUTURE";
        bookingOutDtoTest = bookingService.getBookingsForItemsByOwnerId(user1.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.returnUserDto(user1));
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(Status.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));
        state = "WAITING";
        bookingOutDtoTest = bookingService.getBookingsForItemsByOwnerId(user1.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.returnUserDto(user1));

        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(Status.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(booking1)));
        state = "REJECTED";
        bookingOutDtoTest = bookingService.getBookingsForItemsByOwnerId(user1.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), booking1.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.returnUserDto(user1));
    }

    @Test
    void testGetBookingsForAllItemsByOwnerIdNotHaveItems() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findByOwnerId(anyLong())).thenReturn(List.of());

        assertThrows(ValidationException.class, () -> bookingService.getBookingsForItemsByOwnerId(user1.getId(), "APPROVED", 5, 10));
    }
}