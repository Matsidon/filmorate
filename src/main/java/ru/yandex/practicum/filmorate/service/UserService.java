package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public Set<User> addFriend(Long userId, Long friendId) {
        if (userStorage.getUserById(userId) != null && userStorage.getUserById(friendId) != null) {
            userStorage.getUserById(userId).getFriends().add(friendId);
            userStorage.getUserById(friendId).getFriends().add(userId);
        } else {
            throw new IllegalArgumentException("Некорректный id");
        }
        return userStorage.getSetUsersById(userStorage.getUserById(userId).getFriends());
    }

    public Set<User> removeFriend(Long userId, Long friendId) {
        if (userStorage.getUserById(userId) != null && userStorage.getUserById(friendId) != null) {
            if (userStorage.getUserById(userId).getFriends().remove(friendId)
                    && userStorage.getUserById(friendId).getFriends().remove(userId)) {
                return userStorage.getSetUsersById(userStorage.getUserById(userId).getFriends());
            } else {
                throw new IllegalArgumentException("Пользователей не было в списке друзей");
            }
        } else {
            throw new IllegalArgumentException("Некорректный id");
        }
    }

    public Set<User> getFriendsList(Long userId) {
        if (userStorage.getUserById(userId) != null) {
            return userStorage.getSetUsersById(userStorage.getUserById(userId).getFriends());
        } else {
            throw new IllegalArgumentException("Некорректный id");
        }
    }

    public Set<User> printListCommonFriends(Long userId, Long otherId) {
        Set<Long> generalFriends = new HashSet<>();
        if (userStorage.getUserById(userId) != null && userStorage.getUserById(otherId) != null) {
            for (Long idFriendUser1 : userStorage.getUserById(userId).getFriends()) {
                if (userStorage.getUserById(otherId).getFriends().contains(idFriendUser1)) {
                    generalFriends.add(idFriendUser1);
                }
            }
        } else {
            throw new IllegalArgumentException("Некорректный id");
        }
        return userStorage.getSetUsersById(generalFriends);
    }
}
