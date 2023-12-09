package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private JacksonTester<OutputBookingDto> jsonTest;

    @Autowired
    private JacksonTester<ShortItemBookingDto> jsonShorDtoTest;

    @Autowired
    private JacksonTester<BookingDto> jsonBookingDtoTest;

    @Test
    void testBookingDto() throws Exception {

        LocalDateTime start = LocalDateTime.of(2022, 8, 4, 0, 0);
        LocalDateTime end = LocalDateTime.of(2022, 8, 4, 12, 0);
        UserDto user = UserDto.builder()
                .id(1L)
                .name("vasiliy")
                .email("vasiliy@yandex.ru")
                .build();
        ItemDto itemDto = ItemDto.builder()
                .id(2L)
                .name("ringOfForce")
                .description("from movie ringOfForce")
                .available(true)
                .build();
        OutputBookingDto outputBookingDto = OutputBookingDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .status(Status.APPROVED)
                .booker(user)
                .item(itemDto)
                .build();
        JsonContent<OutputBookingDto> result = jsonTest.write(outputBookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("vasiliy");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("vasiliy@yandex.ru");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("ringOfForce");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
    }

    @Test
    void testBookingDto1() throws Exception {

        LocalDateTime start = LocalDateTime.of(2022, 8, 4, 0, 0);
        LocalDateTime end = LocalDateTime.of(2022, 8, 4, 12, 0);
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .status(Status.APPROVED)
                .build();
        JsonContent<BookingDto> result = jsonBookingDtoTest.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
    }

    @Test
    void testShortItemBookingDto() throws Exception {

        LocalDateTime start = LocalDateTime.of(2022, 8, 4, 0, 0);
        LocalDateTime end = LocalDateTime.of(2022, 8, 4, 12, 0);
        ShortItemBookingDto shortItemBookingDto = ShortItemBookingDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .bookerId(1L)
                .build();
        JsonContent<ShortItemBookingDto> result = jsonShorDtoTest.write(shortItemBookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
    }

}