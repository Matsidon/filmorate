package ru.yandex.practicum.filmorate.controllers;

import lombok.Getter;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Getter
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService = new FilmService();

    @GetMapping
    List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @PostMapping
    Film createFilm(@RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    Film updateFilm(@RequestBody Film film) {
        return filmService.updateFilm(film);
    }
}
