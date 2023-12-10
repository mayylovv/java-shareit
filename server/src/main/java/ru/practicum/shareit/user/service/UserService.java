package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto, long userId);

    void deleteById(long userId);

    UserDto findById(long userId);

    Collection<UserDto> findAll();

}