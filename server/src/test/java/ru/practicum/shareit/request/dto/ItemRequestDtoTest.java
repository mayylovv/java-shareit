package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestDto() throws Exception {

        LocalDateTime created = LocalDateTime.of(2023, 8, 20, 11, 11);

        ItemDto itemDto = ItemDto.builder()
                .requestId(1L)
                .name("warcraft")
                .description("warcraft game")
                .available(true)
                .comments(Collections.emptyList())
                .build();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("req1 text")
                .created(created)
                .items(List.of(itemDto))
                .build();
        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("req1 text");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(created.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
    }

    @Test
    void testItemRequestDto1() {

        LocalDateTime created = LocalDateTime.of(2023, 8, 20, 11, 11);
        ItemDto itemDto = ItemDto.builder()
                .requestId(1L)
                .name("warcraft")
                .description("warcraft game")
                .available(true)
                .comments(Collections.emptyList())
                .build();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("req1 text")
                .created(created)
                .items(List.of(itemDto))
                .build();
        List<ItemRequestDto> result = List.of(itemRequestDto);

        assertThat(result).size().isEqualTo(1);

    }
}