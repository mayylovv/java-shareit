package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemStorageInMemory implements ItemStorage {

    private long id = 0L;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item add(Item item) {
        Long itemId = ++id;
        item.setId(itemId);
        items.put(itemId, item);
        return item;
    }

    @Override
    public Item update(Long itemId, Long userId, Item item) {
        if (!items.containsKey(itemId)) {
            throw new BadRequestException("Позиция с таким id не найдена");
        }
        Item existingItem = items.get(itemId);
        if (!Objects.equals(existingItem.getOwner().getId(), userId)) {
            throw new NotFoundException("Данная позиция принадлежит пользователю с другим id");
        }
        if (item.getName() != null) {
            existingItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            existingItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            existingItem.setAvailable(item.getAvailable());
        }
        items.put(itemId, existingItem);
        return existingItem;
    }

    @Override
    public Item getItemById(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public Collection<Item> getItemsByUserId(Long userId) {
        return items.values();
    }

    @Override
    public Collection<Item> getItemsByKeyword(String keyword) {
        if (keyword.isBlank()) return List.of();
        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                                item.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                .filter(item -> item.getAvailable().equals(true))
                .collect(Collectors.toList());
    }
}