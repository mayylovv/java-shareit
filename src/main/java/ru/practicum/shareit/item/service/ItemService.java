package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(long userId, ItemDto itemDto);

    ItemDto update(long itemId, long userId, ItemDto itemDto);

    ItemDto getItemById(long itemId, long userId);

    List<ItemDto> getItemsByUserId(long userId);

    List<ItemDto> getItemsByKeyword(String keyword);

    CommentDto postComment(long userId, long itemId, CommentDto commentDto);

}