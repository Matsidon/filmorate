package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    @Test
    void createUser() {
        final UserController userController = new UserController();
        final User user = new User("login", "name", "login@mail.ru", LocalDate.of(2000, 5, 12));
        userController.createUser(user);
        assertEquals(1, userController.getUserService().getUserMap().size());
        assertEquals(1, userController.getUserService().getUserMap().get(1).getId());
        assertEquals("name", userController.getUserService().getUserMap().get(1).getName());
        assertEquals("login", userController.getUserService().getUserMap().get(1).getLogin());
        assertEquals("login@mail.ru", userController.getUserService().getUserMap().get(1).getEmail());
        assertEquals(LocalDate.of(2000, 5, 12), userController.getUserService().getUserMap().get(1).getBirthday());
    }

    @Test
    void createFailNameUser() {
        final UserController userController = new UserController();
        final User user = new User("login", "", "login@mail.ru", LocalDate.of(2000, 5, 12));
        userController.createUser(user);
        assertEquals(1, userController.getUserService().getUserMap().size());
        assertEquals(1, userController.getUserService().getUserMap().get(1).getId());
        assertEquals("login", userController.getUserService().getUserMap().get(1).getName());
        assertEquals("login", userController.getUserService().getUserMap().get(1).getLogin());
        assertEquals("login@mail.ru", userController.getUserService().getUserMap().get(1).getEmail());
        assertEquals(LocalDate.of(2000, 5, 12), userController.getUserService().getUserMap().get(1).getBirthday());
    }

    @Test
    void createFailLoginUser() {
        final UserController userController = new UserController();
        final User user = new User("login Fail", "name", "login@mail.ru", LocalDate.of(2000, 5, 12));
        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void createFailBirthdayUser() {
        final UserController userController = new UserController();
        final User user = new User("login", "name", "login@mail.ru", LocalDate.of(2400, 5, 12));
        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void createFailEmailUser() {
        final UserController userController = new UserController();
        final User user = new User("login", "name", "login mail.ru", LocalDate.of(2000, 5, 12));
        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void updateUser() {
        final UserController userController = new UserController();
        userController.createUser(new User("login", "name", "login@mail.ru", LocalDate.of(2000, 5, 12)));
        final User user = new User("loginUpdate", "nameUpdate", "login@mail.ru", LocalDate.of(2000, 5, 12));
        user.setId(1);
        userController.updateUser(user);
        assertEquals(1, userController.getUserService().getUserMap().size());
        assertEquals(1, userController.getUserService().getUserMap().get(1).getId());
        assertEquals("nameUpdate", userController.getUserService().getUserMap().get(1).getName());
        assertEquals("loginUpdate", userController.getUserService().getUserMap().get(1).getLogin());
        assertEquals("login@mail.ru", userController.getUserService().getUserMap().get(1).getEmail());
        assertEquals(LocalDate.of(2000, 5, 12), userController.getUserService().getUserMap().get(1).getBirthday());
    }

    @Test
    void updateFailIdFilm() {
        final UserController userController = new UserController();
        final User user = new User("loginUpdate", "nameUpdate", "login@mail.ru",
                LocalDate.of(2000, 5, 12));
        user.setId(-1);
        assertThrows(ValidationException.class, () -> userController.updateUser(user));
    }

    @Test
    void getUsers() {
        final UserController userController = new UserController();
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
