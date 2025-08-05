package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class FilmorateApplicationTests {
	@Autowired
	private FilmController filmController;

	@Autowired
	private UserController userController;

	@Autowired
	private ObjectMapper objectMapper;

	private MockMvc mockMvc;

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

		assertThrows(RuntimeException.class, () -> filmController.createFilm(film));
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
	void createInvalidUser() throws Exception {
		User user = new User();
		user.setEmail("неправильный-email");
		user.setLogin("");
		user.setBirthday(LocalDate.now().plusDays(1));

		mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(user)))
				.andExpect(status().isBadRequest());
	}
}