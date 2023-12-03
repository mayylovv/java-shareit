package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testConvertUserDto() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("vasiliy")
                .email("vasiliy@yandex.ru")
                .build();
        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("vasiliy");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("vasiliy@yandex.ru");
    }
}

