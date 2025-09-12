package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class FilmorateApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void contextLoads() {
	}

	@Test
	void createValidFilm() throws Exception {
		Film film = new Film();
		film.setName("Тестовый фильм");
		film.setDescription("Тестовое описание");
		film.setReleaseDate(LocalDate.of(2000, 1, 1));
		film.setDuration(120);

		Mpa mpa = new Mpa();
		mpa.setId(1);
		film.setMpa(mpa);

		mockMvc.perform(post("/films")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(film)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.name").value("Тестовый фильм"));
	}

	@Test
	void createInvalidFilm() throws Exception {
		Film film = new Film();
		film.setName("");
		film.setDescription("Очень длинное описание ".repeat(20));
		film.setReleaseDate(LocalDate.of(1800, 1, 1));
		film.setDuration(-1);

		mockMvc.perform(post("/films")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(film)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void createValidUser() throws Exception {
		User user = new User();
		user.setEmail("test@example.com");
		user.setLogin("testlogin");
		user.setBirthday(LocalDate.of(1990, 1, 1));

		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(user)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.email").value("test@example.com"));
	}

	@Test
	void createInvalidUser() throws Exception {
		User user = new User();
		user.setEmail("неправильный-email");
		user.setLogin("");
		user.setBirthday(LocalDate.now().plusDays(1));

		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(user)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testFilmEndpoints() throws Exception {
		// Создаем фильм
		Film film = new Film();
		film.setName("Тестовый фильм");
		film.setDescription("Тестовое описание");
		film.setReleaseDate(LocalDate.of(2000, 1, 1));
		film.setDuration(120);

		Mpa mpa = new Mpa();
		mpa.setId(1);
		film.setMpa(mpa);

		String filmJson = mockMvc.perform(post("/films")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(film)))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		Film createdFilm = objectMapper.readValue(filmJson, Film.class);

		// Получаем фильм по ID
		mockMvc.perform(get("/films/" + createdFilm.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Тестовый фильм"));

		// Получаем популярные фильмы
		mockMvc.perform(get("/films/popular?count=5"))
				.andExpect(status().isOk());
	}

	@Test
	void testUserEndpoints() throws Exception {
		// Создаем пользователя
		User user = new User();
		user.setEmail("test@example.com");
		user.setLogin("testlogin");
		user.setBirthday(LocalDate.of(1990, 1, 1));

		String userJson = mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(user)))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		User createdUser = objectMapper.readValue(userJson, User.class);

		// Получаем пользователя по ID
		mockMvc.perform(get("/users/" + createdUser.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("test@example.com"));

		// Получаем всех пользователей
		mockMvc.perform(get("/users"))
				.andExpect(status().isOk());
	}

	@Test
	void testMpaEndpoints() throws Exception {
		mockMvc.perform(get("/mpa"))
				.andExpect(status().isOk());

		mockMvc.perform(get("/mpa/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("G"));
	}

	@Test
	void testGenreEndpoints() throws Exception {
		mockMvc.perform(get("/genres"))
				.andExpect(status().isOk());

		mockMvc.perform(get("/genres/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Комедия"));
	}
}