package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ConstraintViolationException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private BookingController bookingController;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    private ItemDto itemDto;

    private UserDto userDto;

    private BookingDto bookingDto;

    private OutputBookingDto outputBookingDto1;

    private OutputBookingDto outputBookingDto2;


    @BeforeEach
    void beforeEach() {

        userDto = UserDto.builder()
                .id(1L)
                .name("vasiliy")
                .email("vasiliy@yandex.ru")
                .build();
        itemDto = ItemDto.builder()
                .requestId(1L)
                .name("ringOfForce")
                .description("from movie ringOfForce")
                .available(true)
                .build();
        bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2023, 9, 4, 0, 0))
                .end(LocalDateTime.of(2023, 9, 4, 12, 0))
                .build();
        outputBookingDto1 = OutputBookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 9, 4, 0, 0))
                .end(LocalDateTime.of(2023, 9, 4, 12, 0))
                .item(itemDto)
                .booker(userDto)
                .status(Status.APPROVED)
                .build();
        outputBookingDto2 = OutputBookingDto.builder()
                .id(2L)
                .start(LocalDateTime.of(2023, 9, 4, 14, 0))
                .end(LocalDateTime.of(2023, 9, 4, 16, 0))
                .item(itemDto)
                .booker(userDto)
                .status(Status.APPROVED)
                .build();
    }


    @Test
    void testApproveBooking() throws Exception {
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(outputBookingDto1);
        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(outputBookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(outputBookingDto1.getStatus().toString()), Status.class))
                .andExpect(jsonPath("$.booker.id", is(outputBookingDto1.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(outputBookingDto1.getItem().getId()), Long.class));

        verify(bookingService, times(1)).approveBooking(1L, 1L, true);
    }

    @Test
    void testGetBookingById() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(outputBookingDto1);
        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(outputBookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(outputBookingDto1.getStatus().toString()), Status.class))
                .andExpect(jsonPath("$.booker.id", is(outputBookingDto1.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(outputBookingDto1.getItem().getId()), Long.class));

        verify(bookingService, times(1)).getBookingById(1L, 1L);
    }

    @Test
    void testGetAllBookingsByBookerId() throws Exception {
        when(bookingService.getBookingsByBookerId(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(List.of(outputBookingDto1, outputBookingDto2));
        mvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(outputBookingDto1, outputBookingDto2))));

        verify(bookingService, times(1)).getBookingsByBookerId(1L, "ALL", 0, 10);
    }

    @Test
    void testGetAllBookingsForAllItemsByOwnerId() throws Exception {
        when(bookingService.getBookingsForItemsByOwnerId(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(List.of(outputBookingDto1, outputBookingDto2));
        mvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(outputBookingDto1, outputBookingDto2))));

        verify(bookingService, times(1)).getBookingsForItemsByOwnerId(1L, "ALL", 0, 10);
    }

    @Test
    public void testToBookingShortDto() {
        Booking booking = BookingMapper.returnBooking(bookingDto);
        assertThrows(NullPointerException.class, () -> BookingMapper.toBookingShortDto(booking));
    }
}