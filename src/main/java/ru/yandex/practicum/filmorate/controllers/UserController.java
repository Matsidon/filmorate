package ru.yandex.practicum.filmorate.controllers;

import lombok.Getter;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@Getter
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService = new UserService();

    @GetMapping
    List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping
    User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }
}
