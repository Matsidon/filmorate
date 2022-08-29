package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    @Test
    void createUser() {
        final UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        final User user = new User("login", "name", "login@mail.ru",
                LocalDate.of(2000, 5, 12));
        userController.createUser(user);
        assertEquals(1, userController.getUserService().getUserStorage().getAllUsers().size());
        assertEquals(1, userController.getUserService().getUserStorage().getUserById(1L).getId());
        assertEquals("name", userController.getUserService().getUserStorage().getUserById(1L).getName());
        assertEquals("login", userController.getUserService().getUserStorage().getUserById(1L).getLogin());
        assertEquals("login@mail.ru", userController.getUserService().getUserStorage().getUserById(1L)
                .getEmail());
        assertEquals(LocalDate.of(2000, 5, 12), userController.getUserService().getUserStorage()
                .getUserById(1L).getBirthday());
    }

    @Test
    void createFailNameUser() {
        final UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        final User user = new User("login", "", "login@mail.ru",
                LocalDate.of(2000, 5, 12));
        userController.createUser(user);
        assertEquals(1, userController.getUserService().getUserStorage().getAllUsers().size());
        assertEquals(1, userController.getUserService().getUserStorage().getUserById(1L).getId());
        assertEquals("login", userController.getUserService().getUserStorage().getUserById(1L).getName());
        assertEquals("login", userController.getUserService().getUserStorage().getUserById(1L).getLogin());
        assertEquals("login@mail.ru", userController.getUserService().getUserStorage().getUserById(1L)
                .getEmail());
        assertEquals(LocalDate.of(2000, 5, 12), userController.getUserService().getUserStorage()
                .getUserById(1L).getBirthday());
    }

    @Test
    void createFailLoginUser() {
        final UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        final User user = new User("login Fail", "name", "login@mail.ru", LocalDate.of(2000, 5, 12));
        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void createFailBirthdayUser() {
        final UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        final User user = new User("login", "name", "login@mail.ru", LocalDate.of(2400, 5, 12));
        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void createFailEmailUser() {
        final UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        final User user = new User("login", "name", "login mail.ru", LocalDate.of(2000, 5, 12));
        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void updateUser() {
        final UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        userController.createUser(new User("login", "name", "login@mail.ru",
                LocalDate.of(2000, 5, 12)));
        final User user = new User("loginUpdate", "nameUpdate", "login@mail.ru",
                LocalDate.of(2000, 5, 12));
        user.setId(1);
        userController.updateUser(user);
        assertEquals(1, userController.getUserService().getUserStorage().getAllUsers().size());
        assertEquals(1, userController.getUserService().getUserStorage().getUserById(1L).getId());
        assertEquals("nameUpdate", userController.getUserService().getUserStorage().getUserById(1L).getName());
        assertEquals("loginUpdate", userController.getUserService().getUserStorage().getUserById(1L)
                .getLogin());
        assertEquals("login@mail.ru", userController.getUserService().getUserStorage().getUserById(1L)
                .getEmail());
        assertEquals(LocalDate.of(2000, 5, 12), userController.getUserService().getUserStorage()
                .getUserById(1L).getBirthday());
    }

    @Test
    void updateFailIdFilm() {
        final UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        final User user = new User("loginUpdate", "nameUpdate", "login@mail.ru",
                LocalDate.of(2000, 5, 12));
        user.setId(-1);
        assertThrows(IllegalArgumentException.class, () -> userController.updateUser(user));
    }

    @Test
    void getUsers() {
        final UserController userController = new UserController(new UserService(new InMemoryUserStorage()));
        final User user = new User("login", "name", "login@mail.ru",
                LocalDate.of(2000, 5, 12));
        userController.createUser(user);
        assertEquals(1, userController.getAllUsers().size());
        assertEquals(1, userController.getAllUsers().get(0).getId());
        assertEquals("login@mail.ru", userController.getAllUsers().get(0).getEmail());
        assertEquals("name", userController.getAllUsers().get(0).getName());
        assertEquals("login", userController.getAllUsers().get(0).getLogin());
        assertEquals(LocalDate.of(2000, 5, 12), userController.getAllUsers().get(0).getBirthday());
    }
}
