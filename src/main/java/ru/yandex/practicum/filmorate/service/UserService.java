package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
public class UserService {
    @NonNull
    private final Map<Integer, User> userMap = new HashMap<>();
    private int userId = 0;

    private int generateId() {
        return ++userId;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    public User createUser(User user) {
        if (isValidate(user)) {
            correctName(user);
            if (!userMap.containsKey(user.getId())) {
                user.setId(generateId());
                userMap.put(user.getId(), user);
            } else {
                log.error("Пользователь уже существует {}", user);
                throw new ValidationException("Пользователь уже существует");
            }
        } else {
            log.error("Проверьте корректность введенных данных {}", user);
            throw new ValidationException("Проверьте корректность введенных данных");
        }
        log.debug("Создан пользователь {}", user);
        return user;
    }


    public User updateUser(User user) {
        if (isValidate(user)) {
            correctName(user);
            if (userMap.containsKey(user.getId())) {
                userMap.put(user.getId(), user);
            } else {
                log.error("Нельзя обновить информацию для нового пользователя {}", user);
                throw new ValidationException("Нельзя обновить информацию для нового пользователя");
            }
        } else {
            log.error("Проверьте корректность введенных данных {}", user);
            throw new ValidationException("Проверьте корректность введенных данных");
        }
        log.debug("Обновлен пользователь {}", user);
        return user;
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
