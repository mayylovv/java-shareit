package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.error.exception.NotFoundEmailException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserServiceImpl userService;

    @MockBean
    private UserRepository userRepository;

    User user1;

    User user2;

    UserDto userDto1;

    UserDto userDto2;


    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(1L)
                .name("vasiliy")
                .email("vasiliy@ya.ru")
                .build();
        userDto1 = UserMapper.returnUserDto(user1);
        user2 = User.builder()
                .id(2L)
                .name("ivan")
                .email("ivan@yandex.ru")
                .build();
        userDto2 = UserMapper.returnUserDto(user2);
    }



    @Test
    void testCreateUser() {
        when(userRepository.save(any(User.class))).thenReturn(user1);
        UserDto userDtoTest = userService.create(userDto1);

        assertEquals(userDtoTest.getId(), userDto1.getId());
        assertEquals(userDtoTest.getName(), userDto1.getName());
        assertEquals(userDtoTest.getEmail(), userDto1.getEmail());

        verify(userRepository, times(1)).save(user1);
    }

    @Test
    void testUpdateUser() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(userRepository.findByEmail(anyString())).thenReturn(List.of(user1));
        when(userRepository.save(any(User.class))).thenReturn(user1);
        userDto1.setName("ivan");
        userDto1.setEmail("ivan@yandex.ru");
        UserDto userDtoUpdated = userService.update(userDto1, 1L);

        assertEquals(userDtoUpdated.getName(), user1.getName());
        assertEquals(userDtoUpdated.getEmail(), user1.getEmail());


        verify(userRepository, times(1)).save(user1);
    }



    @Test
    void testUpdateUserWithNotValidEmail() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(userRepository.findByEmail(anyString())).thenReturn(List.of(user1));
        user1.setEmail("21.RU");

        assertThrows(NotFoundEmailException.class, () -> userService.update(userDto1, 2L));
    }

    @Test
    void testUpdateUserWithEmptyEmail() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(userRepository.findByEmail(anyString())).thenReturn(List.of(user1));
        user1.setEmail("");

        assertThrows(NotFoundEmailException.class, () -> userService.update(userDto1, 2L));
    }


    @Test
    void testDeleteUserById() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        userService.deleteById(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testFindById() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        UserDto userDtoTest = userService.findById(1L);

        assertEquals(userDtoTest.getId(), userDto1.getId());
        assertEquals(userDtoTest.getName(), userDto1.getName());
        assertEquals(userDtoTest.getEmail(), userDto1.getEmail());

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        List<UserDto> userDtoList = userService.findAll();
        assertEquals(userDtoList, List.of(userDto1, userDto2));

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testFindAllUsers2time() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        List<UserDto> userDtoList1 = userService.findAll();
        List<UserDto> userDtoList2 = userService.findAll();

        assertEquals(userDtoList1, List.of(userDto1, userDto2));

        verify(userRepository, times(2)).findAll();
    }

}
