package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.LikesStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final LikesStorage likesStorage;
    private final FriendshipStorage friendshipStorage;

    public Optional<User> getUserById(Long id) {
        if (userStorage.getUserById(id).isEmpty()) {
            log.error("Невозможно получить пользвателя с id = {}", id);
            throw new IllegalArgumentException("Невозможно получить пользвателя с таким id");
        }
        log.debug("Получен пользователь с id = {}", id);
        return userStorage.getUserById(id);
    }

    public List<User> getAllUsers() {
        log.debug("Получен список всех пользователей");
        return userStorage.getAllUsers();
    }

    public User createUser(User user) {
        validateUser(user);
        log.debug("Создан пользователь с name = {}", user.getName());
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        validateUser(user);
        if (userStorage.getUserById(user.getId()).isEmpty()) {
            log.error("Невозможно обновить пользвателя с id = {}", user.getId());
            throw new IllegalArgumentException("Невозможно обновить пользвателя с таким id");
        }
        log.debug("Обновлен пользователь с name = {}", user.getName());
        return userStorage.updateUser(user);
    }

    public boolean deleteUser(User user) {
        if (userStorage.getUserById(user.getId()).isEmpty()) {
            log.error("Невозможно удалить пользвателя с id = {}", user.getId());
            throw new IllegalArgumentException("Невозможно удалить пользвателя с таким id");
        }
        for (Long id : friendshipStorage.getFriendsList(user.getId())) {
            friendshipStorage.removeFriend(id, user.getId());
        }
        likesStorage.removeAllLikesByUser(user.getId());
        log.debug("Удален пользователь с id = {}", user.getId());
        return userStorage.deleteUser(user);
    }

    public Set<User> addFriend(Long userId, Long friendId) {
        if (userStorage.getUserById(userId).isEmpty() || userStorage.getUserById(friendId).isEmpty()) {
            log.error("Некорректный набор id ({} и {})", userId, friendId);
            throw new IllegalArgumentException("Некорректный id");
        }
        log.debug("Для пользователя с id = {} добавлен друг с id = {}", userId, friendId);
        return userStorage.getSetUsersById(friendshipStorage.addFriend(userId, friendId));
    }

    public Set<User> removeFriend(Long userId, Long friendId) {
        if (userStorage.getUserById(userId).isEmpty() && userStorage.getUserById(friendId).isEmpty()) {
            log.error("Некорректный набор id ({} и {})", userId, friendId);
            throw new IllegalArgumentException("Некорректный id");
        }
        if (!friendshipStorage.getFriendsList(userId).contains(friendId)) {
            log.error("Некорректный набор id ({} и {})", userId, friendId);
            throw new IllegalArgumentException("Пользователя не было в списке друзей");
        }
        log.debug("Для пользователя с id = {} удален друг с id = {}", userId, friendId);
        return userStorage.getSetUsersById(friendshipStorage.removeFriend(userId, friendId));
    }

    public Set<User> getFriendsList(Long userId) {
        if (userStorage.getUserById(userId).isPresent()) {
            return userStorage.getSetUsersById(friendshipStorage.getFriendsList(userId));
        } else {
            log.error("Некорректный id = {} пользователя", userId);
            throw new IllegalArgumentException("Некорректный id пользователя");
        }
    }

    public Set<User> printListCommonFriends(Long userId, Long otherId) {
        Set<Long> commonFriends = new HashSet<>();
        if (userStorage.getUserById(userId).isPresent() && userStorage.getUserById(otherId).isPresent()) {
            for (Long userFriend : friendshipStorage.getFriendsList(userId)) {
                if (friendshipStorage.getFriendsList(otherId).contains(userFriend)) {
                    commonFriends.add(userFriend);
                }
            }
        } else {
            log.error("Некорректный набор id ({} и {})", userId, otherId);
            throw new IllegalArgumentException("Некорректный id");
        }
        log.debug("Получен список общих друзей пользователей с id = {} и id = {}", userId, otherId);
        return userStorage.getSetUsersById(commonFriends);
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Ошибка валидации при создании пользователя: некорректное значение поля email = {}",
                    user.getEmail());
            throw new ValidationException("Ошибка валидации при создании пользователя: поле email");
        }
        if (user.getLogin() == null || user.getLogin().contains(" ")) {
            log.error("Ошибка валидации при создании пользователя: некорректное значение поля login = {}",
                    user.getLogin());
            throw new ValidationException("Ошибка валидации при создании пользователя: поле login");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка валидации при создании пользователя: некорректное значение поля birthday = {}",
                    user.getBirthday());
            throw new ValidationException("Ошибка валидации при создании пользователя: поле birthday");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }
}
