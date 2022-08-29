package ru.yandex.practicum.filmorate.controllers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@Getter
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    List<User> getAllUsers() {
        return userService.getUserStorage().getAllUsers();
    }

    @PostMapping
    User createUser(@RequestBody User user) {
        return userService.getUserStorage().createUser(user);
    }

    @PutMapping
    User updateUser(@RequestBody User user) {
        return userService.getUserStorage().updateUser(user);
    }

    @GetMapping("/{id}")
    User getUser(@PathVariable("id") Long id) {
        return userService.getUserStorage().getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    Set<User> addFriendByFriendsList(@PathVariable("id") Long userId, @PathVariable("friendId") Long friendId) {
        return userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    Set<User> removeUserByFriendsList(@PathVariable("id") Long userId, @PathVariable("friendId") Long friendId) {
        return userService.removeFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    Set<User> getFriendsList(@PathVariable("id") Long userId) {
        return userService.getFriendsList(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    Set<User> getCommonFriends(@PathVariable("id") Long userId, @PathVariable("otherId") Long otherId) {
        return userService.printListCommonFriends(userId, otherId);
    }
}
