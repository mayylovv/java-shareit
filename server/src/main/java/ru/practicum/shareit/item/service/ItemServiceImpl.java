package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidationException;

import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    @Override
    @Transactional
    public ItemDto addItem(long userId, ItemDto itemDto) {
        checkUser(userId);
        User user = userRepository.findById(userId).get();
        Item item = ItemMapper.returnItem(itemDto, user);
        if (itemDto.getRequestId() != null) {
            checkRequest(itemDto.getRequestId());
            item.setRequest(itemRequestRepository.findById(itemDto.getRequestId()).get());
        }
        itemRepository.save(item);
        return ItemMapper.returnItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto update(long itemId, long userId, ItemDto itemDto) {
        checkUser(userId);
        User user = userRepository.findById(userId).get();
        checkItem(itemId);
        Item item = ItemMapper.returnItem(itemDto, user);
        item.setId(itemId);
        if (!itemRepository.findByOwnerId(userId).contains(item)) {
            throw new NotFoundException("Объект не был найден у пользователя с id =  " + userId);
        }
        Item newItem = itemRepository.findById(item.getId()).get();
        if (item.getName() != null) {
            newItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            newItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            newItem.setAvailable(item.getAvailable());
        }
        itemRepository.save(newItem);
        return ItemMapper.returnItemDto(newItem);
    }

    @Override
    @Transactional
    public ItemDto getItemById(long itemId, long userId) {
        checkItem(itemId);
        Item item = itemRepository.findById(itemId).get();
        ItemDto itemDto = ItemMapper.returnItemDto(item);
        checkUser(userId);
        if (item.getOwner().getId() == userId) {
            Optional<Booking> lastBooking = bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(itemId, Status.APPROVED, LocalDateTime.now());
            Optional<Booking> nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(itemId, Status.APPROVED, LocalDateTime.now());
            if (lastBooking.isPresent()) {
                itemDto.setLastBooking(BookingMapper.toBookingShortDto(lastBooking.get()));
            } else {
                itemDto.setLastBooking(null);
            }
            if (nextBooking.isPresent()) {
                itemDto.setNextBooking(BookingMapper.toBookingShortDto(nextBooking.get()));
            } else {
                itemDto.setNextBooking(null);
            }
        }
        List<Comment> commentList = commentRepository.findByItemId(itemId);
        if (!commentList.isEmpty()) {
            itemDto.setComments(CommentMapper.returnCommentDtoList(commentList));
        } else {
            itemDto.setComments(Collections.emptyList());
        }
        return itemDto;
    }

    @Override
    @Transactional
    public List<ItemDto> getItemsByUserId(long userId, Integer from, Integer size) {
        checkUser(userId);
        PageRequest pageRequest = checkPageSize(from, size);
        List<ItemDto> resultList = new ArrayList<>();
        for (ItemDto itemDto : ItemMapper.returnItemDtoList(itemRepository.findByOwnerId(userId, pageRequest))) {
            Optional<Booking> lastBooking = bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(itemDto.getId(), Status.APPROVED, LocalDateTime.now());
            Optional<Booking> nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(itemDto.getId(), Status.APPROVED, LocalDateTime.now());
            if (lastBooking.isPresent()) {
                itemDto.setLastBooking(BookingMapper.toBookingShortDto(lastBooking.get()));
            } else {
                itemDto.setLastBooking(null);
            }

            if (nextBooking.isPresent()) {
                itemDto.setNextBooking(BookingMapper.toBookingShortDto(nextBooking.get()));
            } else {
                itemDto.setNextBooking(null);
            }
            resultList.add(itemDto);
        }
        for (ItemDto itemDto : resultList) {
            List<Comment> commentList = commentRepository.findByItemId(itemDto.getId());
            if (!commentList.isEmpty()) {
                itemDto.setComments(CommentMapper.returnCommentDtoList(commentList));
            } else {
                itemDto.setComments(Collections.emptyList());
            }
        }
        return resultList;
    }

    @Override
    @Transactional
    public List<ItemDto> getItemsByKeyword(String text, Integer from, Integer size) {
        PageRequest pageRequest = checkPageSize(from, size);
        if (text.equals("")) {
            return Collections.emptyList();
        } else {
            return ItemMapper.returnItemDtoList(itemRepository.search(text, pageRequest));
        }
    }

    @Override
    @Transactional
    public CommentDto postComment(long userId, long itemId, CommentDto commentDto) {
        checkUser(userId);
        User user = userRepository.findById(userId).get();

        checkItem(itemId);
        Item item = itemRepository.findById(itemId).get();

        LocalDateTime dateTime = LocalDateTime.now();

        Optional<Booking> booking = bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(itemId, userId, Status.APPROVED, dateTime);

        if (commentDto.getText().isEmpty()) {
            throw new ValidationException("Невозможно опубликовать пустой комментарий");
        }
        if (booking.isEmpty()) {
            throw new ValidationException(String.format("Пользователь с id = %d не бронировал позицию с id = %d.", userId, itemId));
        }

        Comment comment = CommentMapper.returnComment(commentDto, item, user, dateTime);
        commentRepository.save(comment);

        return CommentMapper.returnCommentDto(comment);
    }

    private void checkUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
    }

    private void checkItem(long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("Позиция с id = " + itemId + " не найдена");
        }

    }

    private PageRequest checkPageSize(Integer from, Integer size) {
        if (from == 0 && size == 0) {
            throw new ValidationException("size и from не должны быть равны нулю");
        }
        if (size <= 0) {
            throw new ValidationException("size должен быть больше нуля");
        }
        if (from < 0) {
            throw new ValidationException("from должно быть больше или равно нулю");
        }
        return PageRequest.of(from / size, size);
    }

    private void checkRequest(Long requestId) {
        if (!itemRequestRepository.existsById(requestId)) {
            throw new NotFoundException("Запрос с id = " + requestId + " не найден.");
        }
    }
}