package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.exception.NotFoundEmailException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;


import java.util.List;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        User user = UserMapper.returnUser(userDto);
        userRepository.save(user);
        return UserMapper.returnUserDto(user);
    }


    @Override
    @Transactional
    public UserDto update(UserDto userDto, long userId) {
        User user = UserMapper.returnUser(userDto);
        user.setId(userId);
        checkUser(userId);
        User newUser = userRepository.findById(userId).get();
        if (user.getName() != null) {
            newUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            List<User> findEmail = userRepository.findByEmail(user.getEmail());
            if (!findEmail.isEmpty() && findEmail.get(0).getId() != userId) {
                throw new NotFoundEmailException("Пользователь с email: " + user.getEmail() + " уже существует");
            }
            newUser.setEmail(user.getEmail());
        }
        userRepository.save(newUser);
        return UserMapper.returnUserDto(newUser);
    }

    @Override
    @Transactional
    public void deleteById(long userId) {
        checkUser(userId);
        userRepository.deleteById(userId);
    }


    @Override
    @Transactional(readOnly = true)
    public UserDto findById(long userId) {
        checkUser(userId);
        return UserMapper.returnUserDto(userRepository.findById(userId).get());
    }

    @Override
    public List<UserDto> findAll() {
        return UserMapper.returnUserDtoList(userRepository.findAll());
    }

    private void checkUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с ID %d не найден", userId));
        }
    }

}