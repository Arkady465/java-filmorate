package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmorateApplicationTests {
	@Autowired
	private FilmController filmController;

	@Autowired
	private UserController userController;

	@Test
	void contextLoads() {
	}

	@Test
	void createValidFilm() {
		Film film = new Film();
		film.setName("Тестовый фильм");
		film.setDescription("Тестовое описание");
		film.setReleaseDate(LocalDate.of(2000, 1, 1));
		film.setDuration(120);

		Film created = filmController.createFilm(film);
		assertNotNull(created.getId());
	}

	@Test
	void createInvalidFilm() {
		Film film = new Film();
		film.setName("");
		film.setDescription("Очень длинное описание ".repeat(20));
		film.setReleaseDate(LocalDate.of(1800, 1, 1));
		film.setDuration(-1);

		assertThrows(ValidationException.class, () -> filmController.createFilm(film));
	}

	@Test
	void createValidUser() {
		User user = new User();
		user.setEmail("test@example.com");
		user.setLogin("testlogin");
		user.setBirthday(LocalDate.of(1990, 1, 1));

		User created = userController.createUser(user);
		assertNotNull(created.getId());
	}

	@Test
	void createInvalidUser() {
		User user = new User();
		user.setEmail("неправильный-email");
		user.setLogin("");
		user.setBirthday(LocalDate.now().plusDays(1));

		assertThrows(ValidationException.class, () -> userController.createUser(user));
	}
}