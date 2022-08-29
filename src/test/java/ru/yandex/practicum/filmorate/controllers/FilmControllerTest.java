package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class FilmControllerTest {
    @Test
    void createFilm() {
        final FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage()));
        final Film film = new Film("name", "description",
                LocalDate.of(2000, 5, 12), 70);
        filmController.createFilm(film);
        assertEquals(1, filmController.getFilmService().getFilmStorage().getAllFilms().size());
        assertEquals(1, filmController.getFilmService().getFilmStorage().getFilmById(1L).getId());
        assertEquals(70, filmController.getFilmService().getFilmStorage().getFilmById(1L).getDuration());
        assertEquals("name", filmController.getFilmService().getFilmStorage().getFilmById(1L).getName());
        assertEquals("description", filmController.getFilmService().getFilmStorage().getFilmById(1L)
                .getDescription());
        assertEquals(LocalDate.of(2000, 5, 12), filmController.getFilmService().getFilmStorage()
                .getFilmById(1L).getReleaseDate());
    }

    @Test
    void createFailNameFilm() {
        final FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage()));
        final Film film = new Film("", "description", LocalDate.of(2000, 5, 12), 70);
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    void createFailDescriptionFilm() {
        final FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage()));
        final Film film = new Film("name", "Пятеро друзей ( комик-группа «Шарло»), " +
                "приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, " +
                "который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия», " +
                "стал кандидатом Коломбани.", LocalDate.of(2000, 5, 12), 70);
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    void createFailReleaseDateFilm() {
        final FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage()));
        final Film film = new Film("name", "description",
                LocalDate.of(1800, 5, 12), 70);
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    void createFailDurationFilm() {
        final FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage()));
        final Film film = new Film("name", "description",
                LocalDate.of(2000, 5, 12), -70);
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    void updateFilm() {
        final FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage()));
        filmController.createFilm(new Film("name", "description",
                LocalDate.of(2000, 5, 12), 70));
        final Film film = new Film("nameUpdate", "descriptionUpdate",
                LocalDate.of(2000, 5, 12), 70);
        film.setId(1);
        filmController.updateFilm(film);
        assertEquals(1, filmController.getFilmService().getFilmStorage().getAllFilms().size());
        assertEquals(1, filmController.getFilmService().getFilmStorage().getFilmById(1L).getId());
        assertEquals(70, filmController.getFilmService().getFilmStorage().getFilmById(1L).getDuration());
        assertEquals("nameUpdate", filmController.getFilmService().getFilmStorage().getFilmById(1L)
                .getName());
        assertEquals("descriptionUpdate", filmController.getFilmService().getFilmStorage().getFilmById(1L)
                .getDescription());
        assertEquals(LocalDate.of(2000, 5, 12), filmController.getFilmService().getFilmStorage()
                .getFilmById(1L).getReleaseDate());
    }

    @Test
    void updateFailIdFilm() {
        final FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage()));
        final Film film = new Film("name", "description", LocalDate.of(2000, 5, 12), 70);
        assertThrows(IllegalArgumentException.class, () -> filmController.updateFilm(film));
    }

    @Test
    void getFilms() {
        final FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage()));
        final Film film = new Film("name", "description", LocalDate.of(2000, 5, 12), 70);
        filmController.createFilm(film);
        assertEquals(1, filmController.getAllFilms().size());
        assertEquals(1, filmController.getAllFilms().get(0).getId());
        assertEquals(70, filmController.getAllFilms().get(0).getDuration());
        assertEquals("name", filmController.getAllFilms().get(0).getName());
        assertEquals("description", filmController.getAllFilms().get(0).getDescription());
        assertEquals(LocalDate.of(2000, 5, 12), filmController.getAllFilms().get(0).getReleaseDate());
    }
}
