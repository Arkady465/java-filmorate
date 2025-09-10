package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class})
public class UserDbStorageTest {

    @Autowired
    private UserDbStorage userDbStorage;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setLogin("testlogin");
        testUser.setName("Test User");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    public void testCreateUser() {
        User createdUser = userDbStorage.create(testUser);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isPositive();
        assertThat(createdUser.getEmail()).isEqualTo(testUser.getEmail());
    }

    @Test
    public void testGetUserById() {
        User createdUser = userDbStorage.create(testUser);
        Optional<User> foundUser = userDbStorage.getById(createdUser.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo(testUser.getEmail());
    }

    @Test
    public void testUpdateUser() {
        User createdUser = userDbStorage.create(testUser);
        createdUser.setName("Обновленное имя");

        User updatedUser = userDbStorage.update(createdUser);

        assertThat(updatedUser.getName()).isEqualTo("Обновленное имя");
    }

    @Test
    public void testGetAllUsers() {
        userDbStorage.create(testUser);

        User anotherUser = new User();
        anotherUser.setEmail("another@example.com");
        anotherUser.setLogin("anotherlogin");
        anotherUser.setName("Another User");
        anotherUser.setBirthday(LocalDate.of(1985, 5, 15));

        userDbStorage.create(anotherUser);

        assertThat(userDbStorage.getAll()).hasSize(2);
    }
}