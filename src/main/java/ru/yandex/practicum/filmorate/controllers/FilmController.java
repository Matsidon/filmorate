package ru.yandex.practicum.filmorate.controllers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.Optional;

@Getter
@RequestMapping
@RequiredArgsConstructor
@RestController
public class FilmController {
    private final FilmService filmService;

    @GetMapping("/films")
    List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @PostMapping("/films")
    Film createFilm(@RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping("/films")
    Film updateFilm(@RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @DeleteMapping("/films")
    boolean deleteFilm(@RequestBody Film film) {
        return filmService.deleteFilm(film);
    }

    @GetMapping("/films/{id}")
    Optional<Film> getFilmById(@PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    @PutMapping("/films/{id}/like/{userId}")
    long addLikeByUser(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    long deleteLikeByUser(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.removeLike(id, userId);
    }

    @GetMapping("/films/popular")
    List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.printTop10Films(count);
    }

    @GetMapping("/genres/{id}")
    Genre getGenre(@PathVariable int id) {
        return filmService.getGenre(id);
    }

    @GetMapping("/genres")
    List<Genre> getAllGenres() {
        return filmService.getAllGenres();
    }

    @GetMapping("/mpa/{id}")
    MPA getMPA(@PathVariable int id) {
        return filmService.getMPA(id);
    }

    @GetMapping("/mpa")
    List<MPA> getAllMPA() {
        return filmService.getAllMPA();
    }
}
