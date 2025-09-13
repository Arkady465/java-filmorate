package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.GenreDbStorage;
import java.util.List;

@RestController
@RequestMapping("/genres")
@Slf4j
public class GenreController {
    private final GenreDbStorage genreDbStorage;

    @Autowired
    public GenreController(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    @GetMapping
    public List<Genre> getAllGenres() {
        log.info("Получен запрос на получение всех жанров");
        return genreDbStorage.getAll();
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable int id) {
        log.info("Получен запрос на получение жанра с id: {}", id);
        return genreDbStorage.getById(id)
                .orElseThrow(() -> new ru.yandex.practicum.filmorate.exceptions.NotFoundException(
                        "Жанр с id " + id + " не найден"));
    }
}