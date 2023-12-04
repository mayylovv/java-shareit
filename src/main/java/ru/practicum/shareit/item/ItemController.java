package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";


    @PostMapping
    public ResponseEntity<ItemDto> addNewItem(@RequestBody @Valid ItemDto itemDto, @RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("add new item {} to user {}", itemDto, userId);
        return ResponseEntity.ok(itemService.addItem(userId, itemDto));
    }


    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> changeItem(@PathVariable Long itemId, @RequestHeader(HEADER_USER_ID) Long userId,
                                              @RequestBody ItemDto itemDto) {
        log.info("update item {}", itemDto);
        return ResponseEntity.ok(itemService.update(itemId, userId, itemDto));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long itemId, @RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("get item by itemId {}", itemId);
        return ResponseEntity.ok(itemService.getItemById(itemId, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItems(@RequestHeader(HEADER_USER_ID) Long userId,
                                                     @RequestParam(required = false, defaultValue = "0") Integer from,
                                                     @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("get all items by userId {}", userId);
        return ResponseEntity.ok(itemService.getItemsByUserId(userId, from, size));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> getItemsByKeyword(@RequestParam String text,
                                                           @RequestParam(required = false, defaultValue = "0") Integer from,
                                                           @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("get items by keyword: {}", text);
        return ResponseEntity.ok(itemService.getItemsByKeyword(text, from, size));
    }


    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> postComment(@RequestHeader(HEADER_USER_ID) Long userId, @PathVariable Long itemId,
                                                  @RequestBody @Valid CommentDto commentDto) {
        log.info("post comment {}", commentDto);
        return ResponseEntity.ok(itemService.postComment(userId, itemId, commentDto));
    }
}