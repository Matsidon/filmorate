package ru.yandex.practicum.filmorate.storage;

import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
@Data
public class InMemoryUserStorage implements UserStorage {
    @NonNull
    private final Map<Long, User> userMap = new HashMap<>();
    private long generateUserId;

    private long generateId() {
        return ++generateUserId;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User getUserById(Long id) {
        if (userMap.containsKey(id)) {
            return userMap.get(id);
        } else {
            throw new IllegalArgumentException("Некорректный id");
        }
    }

    @Override
    public Set<User> getSetUsersById(Set<Long> setIds) {
        Set<User> userSet = new HashSet<>();
        for (Long id : setIds) {
            userSet.add(userMap.get(id));
        }
        return userSet;
    }

    @Override
    public User createUser(User user) {
        if (isValidate(user)) {
            correctName(user);
            if (!userMap.containsKey(user.getId())) {
                user.setId(generateId());
                userMap.put(user.getId(), user);
            } else {
                log.error("Пользователь уже существует {}", user);
                throw new IllegalArgumentException("Пользователь уже существует");
            }
        } else {
            log.error("Проверьте корректность введенных данных {}", user);
            throw new ValidationException("Проверьте корректность введенных данных");
        }
        log.debug("Создан пользователь {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (isValidate(user)) {
            correctName(user);
            if (userMap.containsKey(user.getId())) {
                Set<Long> friendsOldList = userMap.get(user.getId()).getFriends();
                user.setFriends(friendsOldList);
                userMap.put(user.getId(), user);
            } else {
                log.error("Нельзя обновить информацию для нового пользователя {}", user);
                throw new IllegalArgumentException("Нельзя обновить информацию для пользователя с таким id");
            }
        } else {
            log.error("Проверьте корректность введенных данных {}", user);
            throw new ValidationException("Проверьте корректность введенных данных");
        }
        log.debug("Обновлен пользователь {}", user);
        return user;
    }

    @Override
    public User deleteUser(User user) {
        deleteUserOfFriendList(user);
        userMap.remove(user.getId());
        return user;
    }

    private void deleteUserOfFriendList(User user) {
        Set<Long> friendsOldList = userMap.get(user.getId()).getFriends();
        for (Long friendId : friendsOldList) {
            userMap.get(friendId).getFriends().remove(user.getId());
        }
    }

    private boolean isValidate(User user) {
        return isCorrectEmail(user) && isCorrectLogin(user) && isCorrectBirthday(user);
    }

    private boolean isCorrectEmail(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            return false;
        }
        return user.getEmail().contains("@");
    }

    private boolean isCorrectLogin(User user) {
        if (user.getLogin() == null) {
            return false;
        }
        return !user.getLogin().contains(" ");
    }

    private void correctName(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

    private boolean isCorrectBirthday(User user) {
        return user.getBirthday().isBefore(LocalDate.now());
    }
}
