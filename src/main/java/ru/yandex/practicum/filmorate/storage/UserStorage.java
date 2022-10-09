package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public interface UserStorage {
    Optional<User> getUserById(Long id);

    Set<User> getSetUsersById(Set<Long> setIds);

    List<User> getAllUsers();

    User createUser(User user);

    User updateUser(User user);

    boolean deleteUser(User user);
}
