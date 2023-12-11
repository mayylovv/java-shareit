package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @MockBean
    private UserServiceImpl userService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;


    UserDto userDto1;
    UserDto userDto2;

    @BeforeEach
    void setUp() {

        userDto1 = UserDto.builder()
                .id(1L)
                .name("vasiliy")
                .email("vasiliy@ya.ru")
                .build();
        userDto2 = UserDto.builder()
                .id(2L)
                .name("ivan")
                .email("ivan@yandex.ru")
                .build();
    }

    @Test
    void testCreateUser() throws Exception {
        when(userService.create(any(UserDto.class))).thenReturn(userDto1);
        mvc.perform(post("/users")
                .content(mapper.writeValueAsString(userDto1))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto1.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail()), String.class));

        verify(userService, times(1)).create(userDto1);
    }

    @Test
    void testUpdateUser() throws Exception {
        when(userService.update(any(UserDto.class), anyLong())).thenReturn(userDto2);
        mvc.perform(patch("/users/{userId}", 1L)
                    .content(mapper.writeValueAsString(userDto2))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto2.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto2.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto2.getEmail()), String.class));

        verify(userService, times(1)).update(userDto2, 1L);
    }


    @Test
    void testDeleteUser() throws Exception {
        mvc.perform(delete("/users/{userId}", 1L)).andExpect(status().isOk());

        verify(userService, times(1)).deleteById(1L);
    }

    @Test
    void testFindById() throws Exception {
        when(userService.findById(1L)).thenReturn(userDto1);
        mvc.perform(get("/users/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto1.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail()), String.class));

        verify(userService, times(1)).findById(1L);
    }

    @Test
    void testFindAllUsers() throws Exception {
        when(userService.findAll()).thenReturn(List.of(userDto1, userDto2));
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(userDto1, userDto2))));

        verify(userService, times(1)).findAll();
    }

}
