package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserStorage userStorage;

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

    public void addFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        user.addFriend(friend.getId());
        friend.addFriend(user.getId());
    }

    public void removeFriend(int userId, int badFriendId) {
        User user = userStorage.getUserById(userId);
        User badFriend = userStorage.getUserById(badFriendId);

        user.deleteFriend(badFriend.getId());
        badFriend.deleteFriend(user.getId());
    }

    public List<User> listFriends(int userId) {

        User user = userStorage.getUserById(userId);
        return user.listFriends().stream()
                .map(id -> userStorage.getUserById(id))
                .collect(Collectors.toList());
    }

    public List<User> listCommonFriends(int userId, int friendId) {
        List<User> userFriends = listFriends(userId);
        List<User> friendFriends = listFriends(friendId);

        return userFriends.stream().filter(friendFriends::contains).collect(Collectors.toList());
    }
}
