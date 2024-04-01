package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.models.User;
import java.util.List;

public interface UserStorage {

    List<User> getUsers();

    User getUserById(int userId);

    User addUser(User user);

    void deleteUser(User user);

    User updateUser(User user);

    void addFriend(User user, User friend);

    void removeFriend(User user, User friend);

    List<User> getFriends(User user);
}
