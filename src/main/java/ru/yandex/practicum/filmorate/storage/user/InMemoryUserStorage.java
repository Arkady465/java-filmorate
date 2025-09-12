package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Component ("userInMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int idCounter = 1;

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(idCounter++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> getById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void delete(int id) {
        users.remove(id);
        // Удаляем пользователя из списков друзей других пользователей
        users.values().forEach(u -> u.getFriends().remove(id));
    }

    @Override
    public void addFriend(int userId, int friendId) {
        validateUserExists(userId);
        validateUserExists(friendId);

        User user = users.get(userId);
        user.getFriends().put(friendId, FriendshipStatus.UNCONFIRMED);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        validateUserExists(userId);
        validateUserExists(friendId);

        User user = users.get(userId);
        user.getFriends().remove(friendId);
    }

    @Override
    public List<User> getFriends(int userId) {
        validateUserExists(userId);
        User user = users.get(userId);
        return user.getFriends().keySet().stream()
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        validateUserExists(userId);
        validateUserExists(otherId);

        User user = users.get(userId);
        User otherUser = users.get(otherId);

        return user.getFriends().keySet().stream()
                .filter(otherUser.getFriends().keySet()::contains)
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void validateUserExists(int userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
    }
}
