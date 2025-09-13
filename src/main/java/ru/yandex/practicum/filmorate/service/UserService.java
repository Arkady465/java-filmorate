package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUsers() {
        return userStorage.getAll();
    }

    public User createUser(User user) {
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        return userStorage.update(user);
    }

    public User getUserById(int id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new ru.yandex.practicum.filmorate.exceptions.NotFoundException(
                        "Пользователь с id " + id + " не найден"));
    }

    public void addFriend(int userId, int friendId) {
        // Проверяем что пользователи существуют
        userStorage.getById(userId)
                .orElseThrow(() -> new ru.yandex.practicum.filmorate.exceptions.NotFoundException(
                        "Пользователь с id " + userId + " не найден"));

        userStorage.getById(friendId)
                .orElseThrow(() -> new ru.yandex.practicum.filmorate.exceptions.NotFoundException(
                        "Пользователь с id " + friendId + " не найден"));

        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        // Проверяем что пользователи существуют
        userStorage.getById(userId)
                .orElseThrow(() -> new ru.yandex.practicum.filmorate.exceptions.NotFoundException(
                        "Пользователь с id " + userId + " не найден"));

        userStorage.getById(friendId)
                .orElseThrow(() -> new ru.yandex.practicum.filmorate.exceptions.NotFoundException(
                        "Пользователь с id " + friendId + " не найден"));

        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(int userId) {
        // Проверяем что пользователь существует
        userStorage.getById(userId)
                .orElseThrow(() -> new ru.yandex.practicum.filmorate.exceptions.NotFoundException(
                        "Пользователь с id " + userId + " не найден"));

        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        // Проверяем что пользователи существуют
        userStorage.getById(userId)
                .orElseThrow(() -> new ru.yandex.practicum.filmorate.exceptions.NotFoundException(
                        "Пользователь с id " + userId + " не найден"));

        userStorage.getById(otherId)
                .orElseThrow(() -> new ru.yandex.practicum.filmorate.exceptions.NotFoundException(
                        "Пользователь с id " + otherId + " не найден"));

        return userStorage.getCommonFriends(userId, otherId);
    }
}