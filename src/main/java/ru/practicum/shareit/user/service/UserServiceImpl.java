package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.exceptions.NotFoundEmailException;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        User user = UserMapper.returnUser(userDto);
        userRepository.save(user);
        return UserMapper.returnUserDto(user);
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto, Long userId) {
        User exisitingUser = UserMapper.returnUser(userDto);
        exisitingUser.setId(userId);
        checkUser(userId);
        User user = userRepository.findById(userId).get();
        if (exisitingUser.getName() != null) {
            user.setName(exisitingUser.getName());
        }
        if (exisitingUser.getEmail() != null) {
            List<User> findEmail = userRepository.findByEmail(exisitingUser.getEmail());
            if (!findEmail.isEmpty() && findEmail.get(0).getId() != userId) {
                throw new NotFoundEmailException("Пользователь с email: " + exisitingUser.getEmail() + " уже существует");
            }
            user.setEmail(exisitingUser.getEmail());
        }
        userRepository.save(user);
        return UserMapper.returnUserDto(user);
    }

    @Override
    @Transactional
    public void deleteById(Long userId) {
        checkUser(userId);
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findById(Long userId) {
        checkUser(userId);
        return UserMapper.returnUserDto(userRepository.findById(userId).get());
    }

    @Override
    public List<UserDto> findAll() {
        return UserMapper.returnUserDtoList(userRepository.findAll());
    }

    private void checkUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
    }
}
