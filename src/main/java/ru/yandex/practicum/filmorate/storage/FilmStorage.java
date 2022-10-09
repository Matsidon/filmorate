package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;
import java.util.Optional;

@Component
public interface FilmStorage {
    Optional<Film> getFilmById(Long id);
    List<Film> getAllFilms();
    Film createFilm(Film film);

    Film updateFilm(Film film);

    boolean deleteFilm(Film film);
    Optional<Genre> getGenre(int id);
    List<Genre> getAllGenres();
    Optional<MPA> getMPA(int id);
    List<MPA> getAllMPA();
}
