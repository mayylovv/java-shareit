package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    Item item1;

    Item item2;

    User user;

    @BeforeEach
    void beforeEach() {

        user = userRepository.save(User.builder()
                .id(1L)
                .name("vasiliy")
                .email("vasiliy@yandex.ru")
                .build());
        item1 = itemRepository.save(Item.builder()
                .name("ringOfForce")
                .description("from movie ringOfForce")
                .available(true)
                .owner(user)
                .build());
        item2 = itemRepository.save(Item.builder()
                .name("warcraft")
                .description("warcraft game")
                .available(true)
                .owner(user)
                .build());
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void testSearchInRepository() {

        List<Item> items = itemRepository.search("game", PageRequest.of(0, 1));

        assertEquals(1, items.size());
    }
}