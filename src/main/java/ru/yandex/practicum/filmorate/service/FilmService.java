package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import java.time.LocalDate;
import java.util.List;

@Service
public class FilmService {
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    public Film createFilm(Film film) {
        validateFilm(film);
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        validateFilm(film);
        return filmStorage.update(film);
    }

    public Film getFilmById(int id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new ru.yandex.practicum.filmorate.exceptions.NotFoundException(
                        "Фильм с id " + id + " не найден"));
    }

    public void addLike(int filmId, int userId) {
        // Проверяем что пользователь существует
        userStorage.getById(userId)
                .orElseThrow(() -> new ru.yandex.practicum.filmorate.exceptions.NotFoundException(
                        "Пользователь с id " + userId + " не найден"));

        // Проверяем что фильм существует
        filmStorage.getById(filmId)
                .orElseThrow(() -> new ru.yandex.practicum.filmorate.exceptions.NotFoundException(
                        "Фильм с id " + filmId + " не найден"));

        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        // Проверяем что пользователь существует
        userStorage.getById(userId)
                .orElseThrow(() -> new ru.yandex.practicum.filmorate.exceptions.NotFoundException(
                        "Пользователь с id " + userId + " не найден"));

        // Проверяем что фильм существует
        filmStorage.getById(filmId)
                .orElseThrow(() -> new ru.yandex.practicum.filmorate.exceptions.NotFoundException(
                        "Фильм с id " + filmId + " не найден"));

        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopular(count);
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate() == null) {
            throw new ValidationException("Дата релиза обязательна");
        }

        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }
}