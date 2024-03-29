package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;
    @Autowired
    public UserService(@Qualifier("Repository") UserStorage userStorage) {
        this.userStorage = userStorage;
    }
    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User addFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        userStorage.addFriend(user, friend);

        return userStorage.getUserById(user.getId());
    }

    public void removeFriend(int userId, int badFriendId) {
        User user = userStorage.getUserById(userId);
        User badFriend = userStorage.getUserById(badFriendId);

        userStorage.removeFriend(user, badFriend);
    }

    public List<User> listFriends(int userId) {

        User user = userStorage.getUserById(userId);

        return userStorage.getFriends(user);
    }

    public List<User> listCommonFriends(int userId, int friendId) {
        List<User> userFriends = listFriends(userId);
        List<User> friendFriends = listFriends(friendId);

        return userFriends.stream().filter(friendFriends::contains).collect(Collectors.toList());
    }
}
