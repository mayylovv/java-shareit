package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;


@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<ItemRequestDto> createRequest(@RequestHeader(HEADER_USER_ID) Long userId,
                                                        @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("User {}, add new request", userId);
        return ResponseEntity.ok(itemRequestService.createRequest(itemRequestDto, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getAllRequestsByUserId(@RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Get user Id {}", userId);
        return ResponseEntity.ok(itemRequestService.getAllRequestsByUserId(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAllRequests(@RequestHeader(HEADER_USER_ID) Long userId,
                                                               @RequestParam(defaultValue = "0", required = false) Integer from,
                                                               @RequestParam(defaultValue = "10", required = false) Integer size) {
        log.info("Get all requests by All users ");
        return ResponseEntity.ok(itemRequestService.getAllRequests(userId, from, size));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getRequestById(@RequestHeader(HEADER_USER_ID) Long userId,
                                                         @PathVariable("requestId") Long requestId) {
        log.info("Get request {}", requestId);
        return ResponseEntity.ok(itemRequestService.getRequestById(userId, requestId));
    }
}