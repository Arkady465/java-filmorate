package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Film {
    int id;

    @NotBlank(message = "Название не может быть пустым")
    String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    String description;

    @NotNull(message = "Дата релиза не может быть пустой")
    LocalDate releaseDate;

    @Positive(message = "Продолжительность должна быть положительным числом")
    int duration;

    Mpa mpa;

    Set<Genre> genres = new HashSet<>();

    @JsonIgnore
    Set<Integer> likes = new HashSet<>();

    @JsonIgnore
    int likesCount;
}