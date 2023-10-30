package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {

    Item add(Item item);

    Item update(Long itemId, Long userId, Item itemUpdate);

    Item getItemById(Long itemId);

    Collection<Item> getItemsByUserId(Long userId);

    Collection<Item> getItemsByKeyword(String text);
}