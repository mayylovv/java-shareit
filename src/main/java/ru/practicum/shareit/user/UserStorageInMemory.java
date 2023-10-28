package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class UserStorageInMemory implements UserStorage {

    private static long id = 0L;
    HashMap<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        if (users.values().stream().noneMatch(u -> Objects.equals(user.getEmail(), u.getEmail()))) {
            long userId = id++;
            user.setId(userId);
            users.put(userId, user);
            return user;
        } else {
            throw new ValidationException("Пользователь с таким email не найден");
        }
    }

    @Override
    public User update(User user, Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        if (users.values().stream()
                .filter(u -> !Objects.equals(u.getId(), userId))
                .anyMatch(u -> Objects.equals(user.getEmail(), u.getEmail()))) {
            throw new ValidationException("Пользователь с таким email не найден");
        }
        User existingUser = users.get(userId);
        user.setId(userId);
        if (user.getName() == null) {
            user.setName(existingUser.getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(existingUser.getEmail());
        }
        users.put(userId, user);
        return user;
    }

    @Override
    public void deleteByID(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        users.remove(userId);
    }

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }
}
