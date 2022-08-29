package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
@Component
public interface FilmStorage {
    Film getFilmById(Long id);
    List<Film> getAllFilms();
    Film createFilm(Film film);

    Film updateFilm(Film film);

    Film deleteFilm(Film film);
}
