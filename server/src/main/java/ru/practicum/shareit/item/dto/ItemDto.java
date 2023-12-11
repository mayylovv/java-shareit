package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.ShortItemBookingDto;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.util.List;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {

    Long id;
    String name;
    String description;
    Boolean available;
    ShortItemBookingDto lastBooking;
    ShortItemBookingDto nextBooking;
    List<CommentDto> comments;
    Long requestId;
}