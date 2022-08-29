package ru.yandex.practicum.filmorate.controllers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.Set;

@Getter
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    List<Film> getAllFilms() {
        return filmService.getFilmStorage().getAllFilms();
    }

    @PostMapping
    Film createFilm(@RequestBody Film film) {
        return filmService.getFilmStorage().createFilm(film);
    }

    @PutMapping
    Film updateFilm(@RequestBody Film film) {
        return filmService.getFilmStorage().updateFilm(film);
    }

    @GetMapping("/{id}")
    Film getFilmById(@PathVariable Long id) {
        return filmService.getFilmStorage().getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    Set<Long> addLikeByUser(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    Set<Long> deleteLikeByUser(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.printTop10Films(count);
    }
}
