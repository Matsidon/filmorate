package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
public class FilmService {
    @NonNull
    private final Map<Integer, Film> filmMap = new HashMap<>();
    public static final LocalDate START_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private int filmId = 0;

    private int generateId() {
        return ++filmId;
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(filmMap.values());
    }

    public Film createFilm(Film film) {
        if (isValidate(film)) {
            if (!filmMap.containsKey(film.getId())) {
                film.setId(generateId());
                filmMap.put(film.getId(), film);
            } else {
                log.error("Фильм уже был добавлен раннее {}", film);
                throw new ValidationException("Фильм уже был добавлен раннее");
            }
        } else {
            log.error("Некорректные данные {}", film);
            throw new ValidationException("Некорректные данные");
        }
        log.debug("Добавлен фильм {}", film);
        return film;
    }


    public Film updateFilm(Film film) {
        if (isValidate(film)) {
            if (filmMap.containsKey(film.getId())) {
                filmMap.put(film.getId(), film);
            } else {
                log.error("Фильм не добавлен {}", film);
                throw new ValidationException("Фильм не добавлен");
            }
        } else {
            log.error("Некорректные данные {}", film);
            throw new ValidationException("Некорректные данные");
        }
        log.debug("Обновлен фильм {}", film);
        return film;
    }

    private boolean isValidate(Film film) {
        return isCorrectName(film) && isCorrectDescription(film)
                && isCorrectDuration(film) && isCorrectReleaseDate(film);
    }

    private boolean isCorrectName(Film film) {
        return film.getName() != null && !film.getName().isEmpty();
    }

    private boolean isCorrectDescription(Film film) {
        return film.getDescription().length() <= 200;
    }

    private boolean isCorrectReleaseDate(Film film) {
        return film.getReleaseDate().isAfter(START_RELEASE_DATE);
    }

    private boolean isCorrectDuration(Film film) {
        return film.getDuration() > 0;
    }
}
