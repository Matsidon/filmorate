package ru.yandex.practicum.filmorate.storage;

import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
@Data
public class InMemoryFilmStorage implements FilmStorage {
    @NonNull
    private final Map<Long, Film> filmMap = new HashMap<>();
    public static final LocalDate START_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private long generateFilmId = 0;

    private long generateId() {
        return ++generateFilmId;
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(filmMap.values());
    }

    @Override
    public Film getFilmById(Long id) {
        if (filmMap.containsKey(id)) {
            return filmMap.get(id);
        } else {
            throw new IllegalArgumentException("Некорректный id");
        }
    }

    @Override
    public Film createFilm(Film film) {
        if (isValidate(film)) {
            if (!filmMap.containsKey(film.getId())) {
                film.setId(generateId());
                filmMap.put(film.getId(), film);
            } else {
                log.error("Фильм уже был добавлен раннее {}", film);
                throw new IllegalArgumentException("Фильм уже был добавлен раннее");
            }
        } else {
            log.error("Некорректные данные {}", film);
            throw new ValidationException("Некорректные данные");
        }
        log.debug("Добавлен фильм {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (isValidate(film)) {
            if (filmMap.containsKey(film.getId())) {
                Set<Long> listOldLikes = filmMap.get(film.getId()).getLikes();
                film.setLikes(listOldLikes);
                filmMap.put(film.getId(), film);
            } else {
                log.error("Фильм не добавлен {}", film);
                throw new IllegalArgumentException("Нельзя доавить фильм с таким id");
            }
        } else {
            log.error("Некорректные данные {}", film);
            throw new ValidationException("Некорректные данные");
        }
        log.debug("Обновлен фильм {}", film);
        return film;
    }

    @Override
    public Film deleteFilm(Film film) {
        filmMap.remove(film.getId());
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
