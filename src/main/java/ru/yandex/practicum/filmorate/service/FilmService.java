package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

    public Set<Long> addLike(Long filmId, Long userId) {
        if (filmStorage.getFilmById(filmId) != null && userId > 0) {
            filmStorage.getFilmById(filmId).getLikes().add(userId);
        } else {
            throw new IllegalArgumentException("Некорректный id");
        }
        return filmStorage.getFilmById(filmId).getLikes();
    }

    public Set<Long> removeLike(Long filmId, Long userId) {
        if (filmStorage.getFilmById(filmId) != null && filmStorage.getFilmById(filmId).getLikes().remove(userId)) {
            return filmStorage.getFilmById(filmId).getLikes();
        } else {
            throw new IllegalArgumentException("Некорректный id");
        }
    }

    public List<Film> printTop10Films(Integer count) {
        return filmStorage
                .getAllFilms()
                .stream()
                .sorted((o1, o2) -> -1 * (o1.getLikes().size() - o2.getLikes().size()))
                .limit(count).collect(Collectors.toList());
    }
}