package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.models.User;
import java.util.List;

public interface UserStorage {

    List<User> getUsers();

    User getUserById(int userId);

    User addUser(User user);

    void deleteUser(User user);

    User updateUser(User user);
}
