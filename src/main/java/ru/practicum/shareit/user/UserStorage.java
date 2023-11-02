package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

@Component
public interface UserStorage {

    User create(User user);

    User update(User user, Long userId);

    void deleteByID(Long userId);

    Optional<User> findById(Long userId);

    Collection<User> findAll();
}
