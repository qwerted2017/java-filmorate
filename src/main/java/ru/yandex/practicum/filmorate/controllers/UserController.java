package ru.yandex.practicum.filmorate.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.User;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@Slf4j
public class UserController {
    private final List<User> users;
    private static int userCounter = 0;

    private int countUserId() {
        return ++userCounter;
    }

    @GetMapping
    public List<User> getUsers() {
        return users;
    }

    @PostMapping
    public User createUser(@RequestBody @Valid User user) {

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

    @PutMapping
    public User updateUser(@RequestBody User user) {

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
}
