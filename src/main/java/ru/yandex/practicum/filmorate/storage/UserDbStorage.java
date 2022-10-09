package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Repository
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        SqlRowSet sqlRowSet = jdbcTemplate
                .queryForRowSet("SELECT * FROM users WHERE user_id = ?", id);
        if (sqlRowSet.next()) {
            User user = new User(
                    sqlRowSet.getLong("user_id"),
                    sqlRowSet.getString("user_login"),
                    sqlRowSet.getString("user_name"),
                    sqlRowSet.getString("user_email"),
                    sqlRowSet.getDate("user_birthday").toLocalDate());
            user.setId(id);
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public Set<User> getSetUsersById(Set<Long> setIds) {
        Set<User> users = new HashSet<>();
        for (Long id : setIds) {
            if (getUserById(id).isPresent()) {
                users.add(getUserById(id).get());
            }
        }
        return users;
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "select * from USERS";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    private User makeUser(ResultSet rs) throws SQLException {
        long id = rs.getLong("user_id");
        String login = rs.getString("user_login");
        String name = rs.getString("user_name");
        String email = rs.getString("user_email");
        LocalDate birthday = rs.getDate("user_birthday").toLocalDate();
        return new User(id, login, name, email, birthday);
    }

    @Override
    public User createUser(User user) {
        String sqlQuery = "insert into USERS (USER_LOGIN, USER_NAME, USER_EMAIL, USER_BIRTHDAY) " +
                "values (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"USER_ID"});
            stmt.setString(1, user.getLogin());
            String name = user.getName();
            if (name == null) {
                stmt.setString(2, user.getLogin());
            } else {
                stmt.setString(2, name);
            }
            stmt.setString(3, user.getEmail());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "update USERS " +
                "set USER_LOGIN=?, USER_NAME=?, USER_EMAIL=?, USER_BIRTHDAY=? " +
                "where USER_ID=?";
        jdbcTemplate.update(sqlQuery,
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public boolean deleteUser(User user) {
        String sqlQuery = "delete from USERS where USER_ID=?;" +
                "delete from FRIENDSHIP where USER_ID=? OR FRIEND_ID=?;";
        return jdbcTemplate.update(sqlQuery, user.getId(), user.getId(), user.getId()) > 0;
    }
}
