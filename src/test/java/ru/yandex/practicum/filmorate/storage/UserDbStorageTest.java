package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {
    private final UserDbStorage userStorage;
    private final FriendshipStorage friendshipStorage;
    private final JdbcTemplate jdbcTemplate;

    @AfterEach
    public void clearDb() {
        String sql = "DELETE FROM FRIENDSHIP;" +
                "DELETE FROM USERS";
        jdbcTemplate.update(sql);
    }


    @Test
    public void testFindUserById() {
        User user = new User();
        user.setLogin("login");
        user.setName("name");
        user.setEmail("login@mail.ru");
        user.setBirthday(LocalDate.of(2000, 5, 12));
        User user2 = userStorage.createUser(user);
        Optional<User> userOptional = userStorage.getUserById(user2.getId());
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user1 ->
                        assertThat(user1).hasFieldOrPropertyWithValue("id", user2.getId())
                );
    }

    @Test
    public void testGetSetUsersById() {
        User user1 = new User();
        user1.setLogin("login");
        user1.setName("name");
        user1.setEmail("login@mail.ru");
        user1.setBirthday(LocalDate.of(2000, 5, 12));
        User user2 = new User();
        user2.setLogin("loginUpdate");
        user2.setName("nameUpdate");
        user2.setEmail("loginUpdate@mail.ru");
        user2.setBirthday(LocalDate.of(2000, 5, 12));
        User user1Create = userStorage.createUser(user1);
        User user2Create = userStorage.createUser(user2);
        Set<User> userSet = userStorage.getSetUsersById(Set.of(user1Create.getId(), user2Create.getId()));
        assertThat(userSet)
                .contains(user1Create, user2Create);
    }

    @Test
    public void testGetAllUsers() {
        User user1 = new User();
        user1.setLogin("login");
        user1.setName("name");
        user1.setEmail("login@mail.ru");
        user1.setBirthday(LocalDate.of(2000, 5, 12));
        User user2 = new User();
        user2.setLogin("loginUpdate");
        user2.setName("nameUpdate");
        user2.setEmail("loginUpdate@mail.ru");
        user2.setBirthday(LocalDate.of(2000, 5, 12));
        User user1Create = userStorage.createUser(user1);
        User user2Create = userStorage.createUser(user2);
        List<User> userSet = userStorage.getAllUsers();
        assertThat(userSet)
                .element(0)
                .hasFieldOrPropertyWithValue("id", user1Create.getId());
        assertThat(userSet)
                .element(1)
                .hasFieldOrPropertyWithValue("id", user2Create.getId());
    }

    @Test
    public void testCreateUser() {
        User user1 = new User();
        user1.setLogin("login");
        user1.setName("name");
        user1.setEmail("login@mail.ru");
        user1.setBirthday(LocalDate.of(2000, 5, 12));
        User userCreate = userStorage.createUser(user1);
        user1.setId(userCreate.getId());
        assertThat(userCreate)
                .hasFieldOrPropertyWithValue("id", userCreate.getId())
                .hasFieldOrPropertyWithValue("name", "name");
    }

    @Test
    public void testUpdateUser() {
        User user1 = new User();
        user1.setLogin("login");
        user1.setName("name");
        user1.setEmail("login@mail.ru");
        user1.setBirthday(LocalDate.of(2000, 5, 12));
        User user2 = userStorage.createUser(user1);
        user1.setId(user2.getId());
        User user3 = new User(user1.getId(), "loginUpdate", "nameUpdate", "loginUpdate@mail.ru",
                LocalDate.of(2000, 5, 12));
        User userUpdate = userStorage.updateUser(user3);
        assertThat(userUpdate)
                .hasFieldOrPropertyWithValue("name", "nameUpdate");
    }

    @Test
    public void testDeleteUser() {
        User user1 = new User();
        user1.setLogin("login");
        user1.setName("name");
        user1.setEmail("login@mail.ru");
        user1.setBirthday(LocalDate.of(2000, 5, 12));
        userStorage.createUser(user1);
        boolean deleteUser = userStorage.deleteUser(user1);
        assertTrue(deleteUser);
    }

    @Test
    public void testAddFriend() {
        User user1 = new User();
        user1.setLogin("login");
        user1.setName("name");
        user1.setEmail("login@mail.ru");
        user1.setBirthday(LocalDate.of(2000, 5, 12));
        User user2 = new User();
        user2.setLogin("loginUpdate");
        user2.setName("nameUpdate");
        user2.setEmail("loginUpdate@mail.ru");
        user2.setBirthday(LocalDate.of(2000, 5, 12));
        User user1Create = userStorage.createUser(user1);
        User user2Create = userStorage.createUser(user2);
        Set<Long> friendSet = friendshipStorage.addFriend(user1Create.getId(), user2Create.getId());
        assertThat(friendSet)
                .contains(user2Create.getId());
    }

    @Test
    public void testRemoveFriend() {
        User user1 = new User();
        user1.setLogin("login");
        user1.setName("name");
        user1.setEmail("login@mail.ru");
        user1.setBirthday(LocalDate.of(2000, 5, 12));
        User user2 = new User();
        user2.setLogin("loginUpdate");
        user2.setName("nameUpdate");
        user2.setEmail("loginUpdate@mail.ru");
        user2.setBirthday(LocalDate.of(2000, 5, 12));
        User user1Create = userStorage.createUser(user1);
        User user2Create = userStorage.createUser(user2);
        friendshipStorage.addFriend(user1Create.getId(), user2Create.getId());
        Set<Long> friends = friendshipStorage.removeFriend(user1Create.getId(), user2Create.getId());
        assertThat(friends).isEmpty();
    }

    @Test
    public void testGetFriendsList() {
        User user1 = new User();
        user1.setLogin("login");
        user1.setName("name");
        user1.setEmail("login@mail.ru");
        user1.setBirthday(LocalDate.of(2000, 5, 12));
        User user2 = new User();
        user2.setLogin("loginUpdate");
        user2.setName("nameUpdate");
        user2.setEmail("loginUpdate@mail.ru");
        user2.setBirthday(LocalDate.of(2000, 5, 12));
        User user1Create = userStorage.createUser(user1);
        User user2Create = userStorage.createUser(user2);
        friendshipStorage.addFriend(user1Create.getId(), user2Create.getId());
        Set<Long> friends = friendshipStorage.getFriendsList(user1Create.getId());
        assertThat(friends).contains(user2Create.getId());
    }
}
