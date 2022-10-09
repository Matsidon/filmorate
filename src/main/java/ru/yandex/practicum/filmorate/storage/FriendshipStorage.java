package ru.yandex.practicum.filmorate.storage;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Data
@Repository
public class FriendshipStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendshipStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Set<Long> addFriend(Long userId, Long friendId) {
        String query = "INSERT INTO FRIENDSHIP (USER_ID, FRIEND_ID) VALUES (?,?)";
        jdbcTemplate.update(query, userId, friendId);
        return new HashSet<>(getFriendsList(userId));
    }

    public Set<Long> removeFriend(Long userId, Long friendId) {
        String query = "DELETE FROM FRIENDSHIP WHERE USER_ID =? AND FRIEND_ID=?";
        jdbcTemplate.update(query, userId, friendId);
        return new HashSet<>(getFriendsList(userId));
    }

    public Set<Long> getFriendsList(Long userId) {
        String query = "SELECT FRIEND_ID FROM FRIENDSHIP WHERE USER_ID=?";
        return new HashSet<>(jdbcTemplate.query(query, (rs, rowNum) -> makeFriendsList(rs), userId));
    }

    private Long makeFriendsList(ResultSet rs) throws SQLException {
        return rs.getLong("FRIEND_ID");
    }
}
