package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    public ItemDto add(ItemDto itemDto, long userId) {
        Optional<User> user = userStorage.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        if ((itemDto.getName() == null || itemDto.getDescription() == null || itemDto.getAvailable() == null) ||
                (itemDto.getName().isBlank() || itemDto.getDescription().isBlank())) {
            throw new ValidationException("Обязательные поля не заполнены");
        }
        return ItemMapper.toItemDto(itemStorage.add(ItemMapper.toItem(itemDto, user.get(), null)));
    }

    public ItemDto update(Long itemId, Long userId, ItemDto itemDto) {
        Optional<User> existingUser = userStorage.findById(userId);
        if (existingUser.isEmpty()) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        Item item = ItemMapper.toItem(itemDto, existingUser.get(), null);
        return ItemMapper.toItemDto(itemStorage.update(itemId, userId, item));
    }

    public ItemDto getItemById(Long itemId) {
        return ItemMapper.toItemDto(itemStorage.getItemById(itemId));
    }

    public Collection<ItemDto> getItemsByUserId(Long userId) {
        return itemStorage.getItemsByUserId(userId).stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public Collection<ItemDto> getItemsByKeyword(String keyword) {
        return itemStorage.getItemsByKeyword(keyword).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
