package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ItemRequestServiceTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @MockBean
    private ItemRequestRepository itemRequestRepository;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private UserRepository userRepository;


    private User user1;
    private User user2;
    private ItemRequest itemReq1;
    private ItemRequest itemReq2;
    private ItemRequestDto itemRequestDto;
    private Item item;

    @BeforeEach
    void beforeEach() {
        user1 = User.builder()
                .id(1L)
                .name("vasiliy")
                .email("vasiliy@yandex.ru")
                .build();
        user2 = User.builder()
                .id(2L)
                .name("ivan")
                .email("ivan@yandex.ru")
                .build();
        itemReq1 = ItemRequest.builder()
                .id(1L)
                .description("req1 text")
                .created(LocalDateTime.now())
                .build();
        itemReq2 = ItemRequest.builder()
                .id(2L)
                .description("req2 text")
                .created(LocalDateTime.now())
                .build();
        item = Item.builder()
                .id(1L)
                .name("ringOfForce")
                .description("from movie ringOfForce")
                .available(true)
                .owner(user1)
                .request(itemReq1)
                .build();
        itemRequestDto = ItemRequestDto.builder().description("req1 text").build();
    }

    @Test
    void testCreateRequest() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemReq1);
        ItemRequestDto itemRequestDtoTest = itemRequestService.createRequest(itemRequestDto, user1.getId());

        assertEquals(itemRequestDtoTest.getId(), itemReq1.getId());
        assertEquals(itemRequestDtoTest.getDescription(), itemReq1.getDescription());

        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void testGetRequests() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findByRequesterIdOrderByCreatedAsc(anyLong())).thenReturn(List.of(itemReq1));
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of(item));
        ItemRequestDto itemRequestDtoTest = itemRequestService.getAllRequestsByUserId(user1.getId()).get(0);

        assertEquals(itemRequestDtoTest.getItems().get(0).getId(), item.getId());
        assertEquals(itemRequestDtoTest.getItems().get(0).getName(), item.getName());
        assertEquals(itemRequestDtoTest.getItems().get(0).getDescription(), item.getDescription());
        assertEquals(itemRequestDtoTest.getItems().get(0).getAvailable(), item.getAvailable());

        verify(itemRequestRepository, times(1)).findByRequesterIdOrderByCreatedAsc(anyLong());
    }

    @Test
    void testGetAllRequests() {
        when(itemRequestRepository.findByIdIsNotOrderByCreatedAsc(anyLong(), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(itemReq1)));
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of(item));
        ItemRequestDto itemRequestDtoTest = itemRequestService.getAllRequests(user1.getId(), 5, 10).get(0);

        assertEquals(itemRequestDtoTest.getItems().get(0).getId(), item.getId());
        assertEquals(itemRequestDtoTest.getItems().get(0).getName(), item.getName());
        assertEquals(itemRequestDtoTest.getItems().get(0).getDescription(), item.getDescription());
        assertEquals(itemRequestDtoTest.getItems().get(0).getAvailable(), item.getAvailable());

        verify(itemRequestRepository, times(1)).findByIdIsNotOrderByCreatedAsc(anyLong(), any(PageRequest.class));
    }

    @Test
    void testGetRequestById() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(itemReq1));
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of(item));
        ItemRequestDto itemRequestDtoTest = itemRequestService.getRequestById(user1.getId(), itemReq1.getId());

        assertEquals(itemRequestDtoTest.getId(), itemReq1.getId());
        assertEquals(itemRequestDtoTest.getDescription(), itemReq1.getDescription());
        assertEquals(itemRequestDtoTest.getItems().get(0).getId(), item.getId());
        assertEquals(itemRequestDtoTest.getItems().get(0).getRequestId(), user1.getId());

        verify(itemRequestRepository, times(1)).findById(anyLong());
    }

    @Test
    void testAddItemsToRequest() {
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of(item));
        ItemRequestDto itemRequestDtoTest = itemRequestService.addItemsToRequest(itemReq1);

        assertEquals(itemRequestDtoTest.getItems().get(0).getId(), item.getId());
        assertEquals(itemRequestDtoTest.getItems().get(0).getRequestId(), user1.getId());

        verify(itemRepository, times(1)).findByRequestId(anyLong());
    }
}