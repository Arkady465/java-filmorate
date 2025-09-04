package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Component ("filmInMemoryUserStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private final Map<Integer, Set<Integer>> likes = new HashMap<>();
    private int idCounter = 1;

    @Override
    public List<Film> getAll() {
        return films.values().stream()
                .peek(film -> film.setLikesCount(likes.getOrDefault(film.getId(), Collections.emptySet()).size()))
                .collect(Collectors.toList());
    }

    @Override
    public Film create(Film film) {
        film.setId(idCounter++);
        films.put(film.getId(), film);
        likes.put(film.getId(), new HashSet<>());
        film.setLikesCount(0);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }
        films.put(film.getId(), film);
        film.setLikesCount(likes.getOrDefault(film.getId(), Collections.emptySet()).size());
        return film;
    }

    @Override
    public Optional<Film> getById(int id) {
        return Optional.ofNullable(films.get(id))
                .map(film -> {
                    film.setLikesCount(likes.getOrDefault(id, Collections.emptySet()).size());
                    return film;
                });
    }

    @Override
    public void delete(int id) {
        films.remove(id);
        likes.remove(id);
    }

    @Override
    public void addLike(int filmId, int userId) {
        if (!films.containsKey(filmId)) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
        likes.get(filmId).add(userId);
        films.get(filmId).setLikesCount(likes.get(filmId).size());
    }

    @Override
    public void removeLike(int filmId, int userId) {
        if (!films.containsKey(filmId)) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
        likes.get(filmId).remove(userId);
        films.get(filmId).setLikesCount(likes.get(filmId).size());
    }

    @Override
    public List<Film> getPopular(int count) {
        return films.values().stream()
                .peek(film -> film.setLikesCount(likes.getOrDefault(film.getId(), Collections.emptySet()).size()))
                .sorted((f1, f2) -> Integer.compare(f2.getLikesCount(), f1.getLikesCount()))
                .limit(count)
                .collect(Collectors.toList());
    }
}