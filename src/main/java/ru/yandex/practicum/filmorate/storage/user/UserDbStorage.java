package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Primary
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> values = new HashMap<>();
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        values.put("name", user.getName());
        values.put("birthday", user.getBirthday());

        int id = simpleJdbcInsert.executeAndReturnKey(values).intValue();
        user.setId(id);

        return getById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден после создания"));
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        int updated = jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        if (updated == 0) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }

        return getById(user.getId()).orElseThrow(() -> new NotFoundException("Пользователь не найден после обновления"));
    }

    @Override
    public Optional<User> getById(int id) {
        try {
            String sql = "SELECT * FROM users WHERE id = ?";
            User user = jdbcTemplate.queryForObject(sql, this::mapRowToUser, id);

            if (user != null) {
                loadUserFriends(user);
            }

            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        String sql = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, 'UNCONFIRMED')";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public List<User> getFriends(int userId) {
        String sql = "SELECT u.* FROM users u " +
                "JOIN friends f ON u.id = f.friend_id " +
                "WHERE f.user_id = ?";

        return jdbcTemplate.query(sql, this::mapRowToUser, userId);
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        String sql = "SELECT u.* FROM users u " +
                "JOIN friends f1 ON u.id = f1.friend_id " +
                "JOIN friends f2 ON u.id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";

        return jdbcTemplate.query(sql, this::mapRowToUser, userId, otherId);
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday") != null ?
                rs.getDate("birthday").toLocalDate() : null);
        return user;
    }

    private void loadUserFriends(User user) {
        String sql = "SELECT friend_id, status FROM friends WHERE user_id = ?";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, user.getId());

        Map<Integer, FriendshipStatus> friends = new HashMap<>();
        for (Map<String, Object> row : rows) {
            int friendId = (Integer) row.get("friend_id");
            String status = (String) row.get("status");
            friends.put(friendId, FriendshipStatus.valueOf(status));
        }

        user.setFriends(friends);
    }
}