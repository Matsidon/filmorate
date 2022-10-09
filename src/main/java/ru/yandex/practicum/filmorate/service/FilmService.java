package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikesStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikesStorage likesStorage;

    public static final LocalDate START_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public Optional<Film> getFilmById(Long id) {
        if (filmStorage.getFilmById(id).isEmpty()) {
            log.error("Ошибка при получении фильма с id: {}", id);
            throw new IllegalArgumentException("Невозможно получить фильм с таким id");
        }
        log.debug("Получен фильм с id {}", id);
        return filmStorage.getFilmById(id);
    }

    public List<Film> getAllFilms() {
        log.debug("Получен список фильмов");
        return filmStorage.getAllFilms();
    }

    public Film createFilm(Film film) {
        validateFilm(film);
        log.debug("Создан фильм {}", film.getName());
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        validateFilm(film);
        if (filmStorage.getFilmById(film.getId()).isEmpty()) {
            log.error("Ошибка обновления фильма с id: {}", film.getId());
            throw new IllegalArgumentException("Ошибка обновления фильма");
        }
        log.debug("Обновлен фильм с id {}", film.getId());
        return filmStorage.updateFilm(film);
    }

    public boolean deleteFilm(Film film) {
        likesStorage.removeAllLikesByFilm(film.getId());
        log.debug("Удален фильм с id {}", film.getId());
        return filmStorage.deleteFilm(film);
    }

    public long addLike(Long filmId, Long userId) {
        if (filmStorage.getFilmById(filmId).isPresent() && userStorage.getUserById(userId).isPresent()) {
            likesStorage.addLike(filmId, userId);
        } else {
            log.error("Некорректный набор id фильма {} и пользователя {}", filmId, userId);
            throw new IllegalArgumentException("Некорректный id");
        }
        log.debug("Добавлен лайк фильму {} от пользователя {}", filmId, userId);
        return filmStorage.getFilmById(filmId).get().getRate();
    }

    public long removeLike(Long filmId, Long userId) {
        if (filmStorage.getFilmById(filmId).isPresent() && userStorage.getUserById(userId).isPresent()) {
            likesStorage.removeLike(filmId, userId);
        } else {
            log.error("Некорректный набор id фильма {} и пользователя {}", filmId, userId);
            throw new IllegalArgumentException("Некорректный id");
        }
        log.debug("Удален лайк фильму {} от пользователя {}", filmId, userId);
        return filmStorage.getFilmById(filmId).get().getRate();
    }

    public List<Film> printTop10Films(Integer count) {
        log.debug("Получен список ТОП {} фильмов", count);
        return filmStorage
                .getAllFilms()
                .stream()
                .sorted((o1, o2) -> (int) (-1 * (o1.getRate() - o2.getRate())))
                .limit(count).collect(Collectors.toList());
    }

    public Genre getGenre(int id) {
        if (filmStorage.getGenre(id).isEmpty()) {
            log.error("Недопустимое значение id = {} жанра", id);
            throw new IllegalArgumentException("Недопустимое значение id жанра");
        }
        log.debug("Получен жанр с id = {}", id);
        return filmStorage.getGenre(id).get();
    }

    public List<Genre> getAllGenres() {
        log.debug("Получен список всех жанров фильмов");
        return filmStorage.getAllGenres();
    }

    public MPA getMPA(int id) {
        if (filmStorage.getMPA(id).isEmpty()) {
            log.error("Недопустимое значение id = {} рейтинга", id);
            throw new IllegalArgumentException("Недопустимое значение id");
        }
        log.debug("Получен рейтинг с id = {}", id);
        return filmStorage.getMPA(id).get();
    }

    public List<MPA> getAllMPA() {
        log.debug("Получен список всех рейтингов");
        return filmStorage.getAllMPA();
    }

    private void validateFilm(Film film) {
        if (film.getName() == null && film.getName().isEmpty()) {
            log.error("Ошибка валидации при создании фильма: некорректное значение поля name = {}",
                    film.getName());
            throw new ValidationException("Ошибка валидации при создании фильма: поле name");
        }
        if (film.getDescription().length() > 200) {
            log.error("Ошибка валидации при создании фильма: некорректное значение поля description = {}",
                    film.getDescription());
            throw new ValidationException("Ошибка валидации при создании фильма: полe description");
        }
        if (film.getReleaseDate().isBefore(START_RELEASE_DATE)) {
            log.error("Ошибка валидации при создании фильма: некорректное значение поля releaseDate = {}",
                    film.getReleaseDate());
            throw new ValidationException("Ошибка валидации при создании фильма: поле releaseDate");
        }
        if (film.getDuration() <= 0) {
            log.error("Ошибка валидации при создании фильма: некорректное значение поля duration = {}",
                    film.getDuration());
            throw new ValidationException("Ошибка валидации при создании фильма: поле duration");
        }
    }
}