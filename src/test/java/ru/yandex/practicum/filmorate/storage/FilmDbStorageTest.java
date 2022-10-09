package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikesStorage likesStorage;
    private final JdbcTemplate jdbcTemplate;

    @AfterEach
    public void clearDb() {
        String sql = "DELETE FROM LIKES;" +
                "DELETE FROM FILMS_GENRES;" +
                "DELETE FROM FILMS;"+
                "DELETE FROM USERS;";
        jdbcTemplate.update(sql);
    }

    @Test
    public void testGetFilmById() {
        Film film1 = new Film();
        film1.setName("name");
        film1.setDescription("description");
        film1.setReleaseDate(LocalDate.of(2000, 5, 12));
        film1.setDuration(70);
        film1.setRate(4);
        film1.setGenres(filmStorage.getAllGenres());
        film1.setMpa(filmStorage.getMPA(1).get());
        Film filmCreate = filmStorage.createFilm(film1);
        Optional<Film> filmGetById = filmStorage.getFilmById(filmCreate.getId());
        assertThat(filmGetById)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", filmCreate.getId())
                );


    }

    @Test
    public void testCreateFilm() {
        Film film1 = new Film();
        film1.setName("name");
        film1.setDescription("description");
        film1.setReleaseDate(LocalDate.of(2000, 5, 12));
        film1.setDuration(70);
        film1.setRate(4);
        film1.setGenres(filmStorage.getAllGenres());
        film1.setMpa(filmStorage.getMPA(1).get());
        Film filmCreate = filmStorage.createFilm(film1);
        assertThat(filmCreate).hasFieldOrPropertyWithValue("name", "name");
    }

    @Test
    public void testUpdateFilm() {
        Film film1 = new Film();
        film1.setName("name");
        film1.setDescription("description");
        film1.setReleaseDate(LocalDate.of(2000, 5, 12));
        film1.setDuration(70);
        film1.setRate(4);
        film1.setGenres(filmStorage.getAllGenres());
        film1.setMpa(filmStorage.getMPA(1).get());
        Film filmCreate = filmStorage.createFilm(film1);
        Film film2 = new Film();
        film2.setName("nameUpdate");
        film2.setDescription("descriptionUpdate");
        film2.setReleaseDate(LocalDate.of(2000, 5, 12));
        film2.setDuration(70);
        film2.setRate(4);
        film2.setGenres(filmStorage.getAllGenres());
        film2.setMpa(filmStorage.getMPA(1).get());
        film2.setId(filmCreate.getId());
        Film filmUpdate = filmStorage.updateFilm(film2);
        assertThat(filmUpdate)
                .hasFieldOrPropertyWithValue("name", "nameUpdate");
    }

    @Test
    public void testDeleteFilm() {
        Film film1 = new Film();
        film1.setName("name");
        film1.setDescription("description");
        film1.setReleaseDate(LocalDate.of(2000, 5, 12));
        film1.setDuration(70);
        film1.setRate(4);
        film1.setGenres(filmStorage.getAllGenres());
        film1.setMpa(filmStorage.getAllMPA().get(0));
        Film filmCreate = filmStorage.createFilm(film1);
        boolean deleteFilm = filmStorage.deleteFilm(filmCreate);
        assertTrue(deleteFilm);
    }

    @Test
    public void testAddLike() {
        User user1 = new User();
        user1.setLogin("login");
        user1.setName("name");
        user1.setEmail("login@mail.ru");
        user1.setBirthday(LocalDate.of(2000, 5, 12));
        User userCreate = userStorage.createUser(user1);
        Film film1 = new Film();
        film1.setName("name");
        film1.setDescription("description");
        film1.setReleaseDate(LocalDate.of(2000, 5, 12));
        film1.setDuration(70);
        film1.setRate(4);
        film1.setGenres(filmStorage.getAllGenres());
        film1.setMpa(filmStorage.getAllMPA().get(0));
        Film filmCreate = filmStorage.createFilm(film1);
        boolean addLike = likesStorage.addLike(filmCreate.getId(), userCreate.getId());
        assertTrue(addLike);
    }

    @Test
    public void testRemoveLike() {
        User user1 = new User();
        user1.setLogin("login");
        user1.setName("name");
        user1.setEmail("login@mail.ru");
        user1.setBirthday(LocalDate.of(2000, 5, 12));
        User userCreate = userStorage.createUser(user1);
        Film film1 = new Film();
        film1.setName("name");
        film1.setDescription("description");
        film1.setReleaseDate(LocalDate.of(2000, 5, 12));
        film1.setDuration(70);
        film1.setRate(4);
        film1.setGenres(filmStorage.getAllGenres());
        film1.setMpa(filmStorage.getAllMPA().get(0));
        Film filmCreate = filmStorage.createFilm(film1);
        likesStorage.addLike(filmCreate.getId(), userCreate.getId());
        boolean removeLike = likesStorage.removeLike(filmCreate.getId(), userCreate.getId());
        assertTrue(removeLike);
    }
}
