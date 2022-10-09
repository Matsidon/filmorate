package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LikesStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LikesStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO LIKES(FILM_ID, USER_ID)" +
                "VALUES (?,?);";
        return jdbcTemplate.update(sql, filmId, userId) > 0;
    }

    public boolean removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM LIKES WHERE FILM_ID=? AND USER_ID=?;";
        return jdbcTemplate.update(sql, filmId, userId) > 0;
    }

    public void removeAllLikesByFilm(Long filmId) {
        String sql = "DELETE FROM LIKES WHERE FILM_ID=?;";
        jdbcTemplate.update(sql, filmId);
    }

    public void removeAllLikesByUser(Long userId) {
        String sql = "DELETE FROM LIKES WHERE USER_ID=?;";
        jdbcTemplate.update(sql, userId);
    }
}
