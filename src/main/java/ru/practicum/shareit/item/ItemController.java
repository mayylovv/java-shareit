package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.add(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemDto itemDto) {
        return itemService.update(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public Collection<ItemDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getItemsByKeyword(@RequestParam String keyword) {
        return itemService.getItemsByKeyword(keyword);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handle(final BadRequestException e) {
        log.error("Обработка исключения BadRequestException", e);
        return Map.of("error_message", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handle(final NotFoundException e) {
        log.error("Обработка исключения NotFoundException", e);
        return Map.of("error_message", e.getMessage());
    }
}

