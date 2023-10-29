package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorageInMemory userStorageInMemory;

    @Autowired
    public UserService(UserStorageInMemory userStorageInMemory) {
        this.userStorageInMemory = userStorageInMemory;
    }

    public UserDto create(UserDto userDto) {
        if (userDto.getName() == null) {
            throw new NotFoundException("Поле name равно null");
        }
        if (userDto.getEmail() == null) {
            throw new NotFoundException("Поле email равно null");
        }
        return UserMapper.toUserDto(userStorageInMemory.create(UserMapper.toUser(userDto)));
    }

    public UserDto update(UserDto userDto, Long userId) {
        return UserMapper.toUserDto(userStorageInMemory.update(UserMapper.toUser(userDto), userId));
    }

    public void deleteById(Long userId) {
        userStorageInMemory.deleteByID(userId);
    }

    public UserDto findById(Long userId) {
        return UserMapper.toUserDto(userStorageInMemory.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден")));
    }

    public Collection<UserDto> findAll() {
        return userStorageInMemory.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }
}
