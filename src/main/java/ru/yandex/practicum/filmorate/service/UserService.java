package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User findUserById(Long id) {
        return userStorage.findUserById(id);
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User newUser) {
        return userStorage.update(newUser);
    }

    public void addFriends(Long userId, Long friendsID) {
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendsID);

        user.getFriends().add(friendsID);
        friend.getFriends().add(userId);
    }

    public List<User> getFriends(Long userId) {
        User user = userStorage.findUserById(userId);
        return user.getFriends().stream()
                .map(id -> userStorage.findUserById(id))
                .toList();
    }

    public void deleteFriends(Long userId, Long friendsID) {
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendsID);

        user.getFriends().remove(friendsID);
        friend.getFriends().remove(userId);
    }

    public List<User> commonFriends(Long userId, Long friendsID) {
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendsID);

       return user.getFriends().stream()
               .filter(id -> friend.getFriends().contains(id))
               .map(id -> userStorage.findUserById(id))
               .toList();
    }
}
