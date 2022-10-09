
package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        SqlRowSet sqlRowSet = jdbcTemplate
                .queryForRowSet("SELECT * " +
                        "FROM FILMS " +
                        "LEFT JOIN MPA ON FILMS.FILM_RATING_ID=MPA.RATING_ID " +
                        "WHERE FILM_ID=?", id);
        if (sqlRowSet.next()) {
            Film film = new Film(
                    sqlRowSet.getLong("film_id"),
                    sqlRowSet.getString("film_name"),
                    sqlRowSet.getString("film_description"),
                    sqlRowSet.getDate("film_release_date").toLocalDate(),
                    sqlRowSet.getInt("film_duration"),
                    getLikes(id),
                    new ArrayList<>(getGenres(id)),
                    new MPA(sqlRowSet.getInt("RATING_ID"), sqlRowSet.getString("RATING_NAME")));
            film.setId(id);
            return Optional.of(film);
        }
        return Optional.empty();
    }

    private List<Genre> getGenres(Long id) {
        String sql = "SELECT G2.GENRE_ID, GENRE_NAME " +
                "FROM FILMS_GENRES " +
                "LEFT JOIN GENRES G2 on G2.GENRE_ID = FILMS_GENRES.GENRE_ID " +
                "WHERE FILM_ID=?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenres(rs), id);
    }

    private Genre makeGenres(ResultSet rs) throws SQLException {
        int id = rs.getInt("GENRE_ID");
        String name = rs.getString("GENRE_NAME");
        return new Genre(id, name);
    }

    private long getLikes(Long id) {
        String sql = "SELECT USER_ID FROM LIKES WHERE FILM_ID=?";
        return jdbcTemplate.queryForList(sql, Long.TYPE, id).size();
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT * FROM FILMS LEFT JOIN MPA M on M.RATING_ID = FILMS.FILM_RATING_ID";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        long id = rs.getLong("film_id");
        String name = rs.getString("film_name");
        String description = rs.getString("film_description");
        LocalDate releaseDate = rs.getDate("film_release_date").toLocalDate();
        int duration = rs.getInt("film_duration");
        long rate = getLikes(id);
        List<Genre> genres = getGenres(id);
        MPA mpa = new MPA(rs.getInt("FILM_RATING_ID"), rs.getString("RATING_NAME"));
        return new Film(id, name, description, releaseDate, duration, rate, genres, mpa);
    }

    @Override
    public Film createFilm(Film film) {
        String sqlQuery = "insert into FILMS (FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE_DATE, FILM_DURATION," +
                "FILM_RATE, FILM_RATING_ID) " +
                "values (?,?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"FILM_ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setLong(5, film.getRate());
            stmt.setInt(6, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().longValue());
        if (film.getGenres() != null) {
            createFilmGenres(film);
        }
        return film;
    }

    private void createFilmGenres(Film film) {
        String sqlGenres = "INSERT INTO FILMS_GENRES (FILM_ID, GENRE_ID)" +
                "VALUES (?,?)";
        if (film.getGenres() != null) {
            Set<Genre> uniqueGenres = new HashSet<>(film.getGenres());
            for (Genre genre : uniqueGenres) {
                jdbcTemplate.update(sqlGenres, film.getId(), genre.getId());
            }
        }
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "update FILMS " +
                "set FILM_NAME=?, FILM_DESCRIPTION=?, FILM_RELEASE_DATE=?, FILM_DURATION=?, " +
                "FILM_RATE=?,FILM_RATING_ID=? " +
                "where FILM_ID=?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId());
        if (film.getGenres() != null) {
            updateGenres(film);
        }
        return getFilmById(film.getId()).get();
    }

    private void updateGenres(Film film) {
        if (getGenres(film.getId()).size() > 0) {
            String sql = "DELETE FROM FILMS_GENRES WHERE FILM_ID=?";
            jdbcTemplate.update(sql, film.getId());
        }
        createFilmGenres(film);
    }

    @Override
    public boolean deleteFilm(Film film) {
        String sqlQuery;
        if(getLikes(film.getId())!=0) {
            sqlQuery = "delete from LIKES where FILM_ID=?;" +
                    "delete from FILMS_GENRES where FILM_ID=?;" +
                    "delete from FILMS where FILM_ID=?;";
            return jdbcTemplate.update(sqlQuery, film.getId(), film.getId(), film.getId()) > 0;
        } else{
            sqlQuery = "delete from FILMS_GENRES where FILM_ID=?;" +
                    "delete from FILMS where FILM_ID=?;";
        }
        return jdbcTemplate.update(sqlQuery, film.getId(), film.getId()) > 0;
    }

    @Override
    public Optional<Genre> getGenre(int id) {
        String sql = "SELECT * FROM GENRES WHERE GENRE_ID=?";
        if (jdbcTemplate.query(sql, (rs, rowNum) -> makeGenres(rs), id).isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(jdbcTemplate.query(sql, (rs, rowNum) -> makeGenres(rs), id).get(0));
    }

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM GENRES";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenres(rs));
    }

    @Override
    public Optional<MPA> getMPA(int id) {
        String sql = "SELECT * FROM MPA WHERE RATING_ID=?";
        if (jdbcTemplate.query(sql, (rs, rowNum) -> makeMPA(rs), id).isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(jdbcTemplate.query(sql, (rs, rowNum) -> makeMPA(rs), id).get(0));
    }

    private MPA makeMPA(ResultSet rs) throws SQLException {
        int id = rs.getInt("RATING_ID");
        String name = rs.getString("RATING_NAME");
        return new MPA(id, name);
    }

    @Override
    public List<MPA> getAllMPA() {
        String sql = "SELECT * FROM MPA";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMPA(rs));
    }
}
