package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final List<User> users = new ArrayList<>();
    private static int userCounter = 0;

    private final Map<List<Integer>, Boolean> friendships = new HashMap<>();

    private int countUserId() {
        return ++userCounter;
    }

    @Override
    public List<User> getUsers() {
        return users;
    }

    @Override
    public User getUserById(int userId) {
        return users.stream().filter(user -> user.getId() == userId)
                .findAny()
                .orElseThrow(() -> new NotFoundException("No user found with id " + userId));
    }

    @Override
    public User addUser(User user) {
        if (users.stream().noneMatch(user1 -> user1.getId() == user.getId())) {
            user.setId(countUserId());
            if (user.getName() == null || user.getName().isEmpty()) user.setName(user.getLogin());
            users.add(user);
            log.info("User {} added", user);
        } else {
            String e = String.format("User with id %s exists", user.getId());
            log.error(e);
            throw new ValidationException(e);
        }

        return user;
    }

    @Override
    public void deleteUser(User user) {
        users.remove(user);
    }

    @Override
    public User updateUser(User user) {

        if (users.stream().anyMatch(user1 -> user1.getId() == user.getId())) {
            int index = users.indexOf(user);
            users.set(index, user);
            return user;
        } else {
            String e = String.format("User with id %s not exists", user.getId());
            log.error(e);
            throw new NotFoundException(e);
        }
    }

    @Override
    public void addFriend(User user, User friend) {
        if (!friendships.containsKey(List.of(user.getId(), friend.getId()))) {
            friendships.put(List.of(user.getId(), friend.getId()), false);
        }
        if (friendships.containsKey(List.of(friend.getId(), user.getId()))) {
            friendships.put(List.of(user.getId(), friend.getId()), true);
            friendships.put(List.of(friend.getId(), user.getId()), true);
        }
    }

    @Override
    public void removeFriend(User user, User friend) {
        if (friendships.containsKey(List.of(friend.getId(), user.getId()))) {
            friendships.put(List.of(friend.getId(), user.getId()), true);
        }
        friendships.remove(List.of(user.getId(), friend.getId()));
    }

    @Override
    public List<User> getFriends(User user) {
        List<User> friends = new ArrayList<>();

        for (List<Integer> couple : friendships.keySet()) {
            if (couple.get(0) == user.getId()) {
                int friendId = couple.get(1);
                User friend = getUserById(friendId);
                friends.add(friend);
            }
        }
        return friends;
    }
}
