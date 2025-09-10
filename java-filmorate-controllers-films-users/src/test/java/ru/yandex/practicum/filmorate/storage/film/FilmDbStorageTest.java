package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.time.LocalDate;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class})
public class FilmDbStorageTest {

    @Autowired
    private FilmDbStorage filmDbStorage;

    private Film testFilm;

    @BeforeEach
    void setUp() {
        testFilm = new Film();
        testFilm.setName("Тестовый фильм");
        testFilm.setDescription("Тестовое описание");
        testFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        testFilm.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        testFilm.setMpa(mpa);
    }

    @Test
    public void testCreateFilm() {
        Film createdFilm = filmDbStorage.create(testFilm);

        assertThat(createdFilm).isNotNull();
        assertThat(createdFilm.getId()).isPositive();
        assertThat(createdFilm.getName()).isEqualTo(testFilm.getName());
    }

    @Test
    public void testGetFilmById() {
        Film createdFilm = filmDbStorage.create(testFilm);
        Optional<Film> foundFilm = filmDbStorage.getById(createdFilm.getId());

        assertThat(foundFilm).isPresent();
        assertThat(foundFilm.get().getName()).isEqualTo(testFilm.getName());
    }

    @Test
    public void testUpdateFilm() {
        Film createdFilm = filmDbStorage.create(testFilm);
        createdFilm.setName("Обновленное название");

        Film updatedFilm = filmDbStorage.update(createdFilm);

        assertThat(updatedFilm.getName()).isEqualTo("Обновленное название");
    }

    @Test
    public void testGetAllFilms() {
        filmDbStorage.create(testFilm);

        Film anotherFilm = new Film();
        anotherFilm.setName("Другой фильм");
        anotherFilm.setDescription("Другое описание");
        anotherFilm.setReleaseDate(LocalDate.of(2010, 1, 1));
        anotherFilm.setDuration(90);

        Mpa mpa = new Mpa();
        mpa.setId(2);
        anotherFilm.setMpa(mpa);

        filmDbStorage.create(anotherFilm);

        assertThat(filmDbStorage.getAll()).hasSize(2);
    }
}