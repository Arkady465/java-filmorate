package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.MpaDbStorage;
import java.util.List;

@RestController
@RequestMapping("/mpa")
@Slf4j
public class MpaController {
    private final MpaDbStorage mpaDbStorage;

    @Autowired
    public MpaController(MpaDbStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    @GetMapping
    public List<Mpa> getAllMpa() {
        log.info("Получен запрос на получение всех рейтингов MPA");
        return mpaDbStorage.getAll();
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable int id) {
        log.info("Получен запрос на получение рейтинга MPA с id: {}", id);
        return mpaDbStorage.getById(id)
                .orElseThrow(() -> new ru.yandex.practicum.filmorate.exceptions.NotFoundException(
                        "Рейтинг MPA с id " + id + " не найден"));
    }
}