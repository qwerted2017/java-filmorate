package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.User;

import java.util.List;

@Slf4j
@Qualifier("Repository")
@Component
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getUsers() {
        String sql = "select * from USERS";
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new User(rs.getInt("user_id"), rs.getString("email"), rs.getString("login"),
                        rs.getString("name"), rs.getDate("birthday").toLocalDate()));
    }

    @Override
    public User getUserById(int userId) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("select * from USERS where \"user_id\" = ?", userId);
        if (sqlRowSet.next()) {
            User user = new User(sqlRowSet.getInt("user_id"), sqlRowSet.getString("email"),
                    sqlRowSet.getString("login"), sqlRowSet.getString("name"),
                    sqlRowSet.getDate("birthday").toLocalDate());
            return user;
        } else {
            String e = String.format("No user found with id %s", userId);
            log.error(e);
            throw new NotFoundException(e);
        }
    }

    public User getUserByLogin(String userLogin) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("select * from USERS where \"login\" = ?", userLogin);
        if (sqlRowSet.next()) {
            return new User(sqlRowSet.getInt("user_id"), sqlRowSet.getString("email"), sqlRowSet.getString("login"),
                    sqlRowSet.getString("name"), sqlRowSet.getDate("birthday").toLocalDate());
        } else {
            String e = String.format("No user found with login %s", userLogin);
            log.error(e);
            throw new NotFoundException(e);
        }
    }

    @Override
    public User addUser(User user) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("select * from USERS where \"user_id\" = ?", user.getId());
        if (!sqlRowSet.next()) {
            jdbcTemplate.update("insert into USERS (\"email\", \"login\", \"name\", \"birthday\") values (?,?,?,?)",
                    user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

            // Учитывая, что до факта записи в БД нового пользователя id его мы не знаем (а вернуть id пользователя
            // нужно в ответе), а в ТЗ прямо не указано - "приказом по армии" считаю что двух полоьзователей с одним
            // логином быть не может.

            return getUserByLogin(user.getLogin());
        } else {
            String e = String.format("User with id %s exists", user.getId());
            log.error(e);
            throw new ValidationException(e);
        }
    }

    @Override
    public void deleteUser(User user) {
        if (getUserById(user.getId()) != null) {
            jdbcTemplate.update("delete from USERS where \"user_id\" = ?", user.getId());
        } else {
            String e = String.format("No user found with id %s", user.getId());
            log.error(e);
            throw new NotFoundException(e);
        }
    }

    @Override
    public User updateUser(User user) {
        if (getUserById(user.getId()) != null) {
            jdbcTemplate.update(
                    "update USERS set \"email\" = ?, \"login\" = ?, \"name\" = ?, \"birthday\" =? where \"user_id\" ="
                    + " ?",
                    user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
            return getUserById(user.getId());
        } else {
            String e = String.format("No user found with id %s", user.getId());
            log.error(e);
            throw new NotFoundException(e);
        }
    }

    @Override
    public void addFriend(User user, User friend) {
        SqlRowSet user2Friend = jdbcTemplate.queryForRowSet(
                "select * from FRIENDSHIP where \"user_id\" = ? and \"friend_id\" = ?", user.getId(), friend.getId());
        SqlRowSet friend2User = jdbcTemplate.queryForRowSet(
                "select * from FRIENDSHIP where \"user_id\" = ? and \"friend_id\" = ?", friend.getId(), user.getId());

        if (!user2Friend.next()) {
            jdbcTemplate.update("insert into FRIENDSHIP (\"user_id\", \"friend_id\", \"status\") values (?, ?, ?)",
                    user.getId(), friend.getId(), false);
        } else {
            if (friend2User.next()) {
                jdbcTemplate.update("insert into FRIENDSHIP (\"user_id\", \"friend_id\", \"status\") values (?, ?, ?)",
                        friend.getId(), user.getId(), true);
            }

            jdbcTemplate.update("insert into FRIENDSHIP (\"user_id\", \"friend_id\", \"status\") values (?, ?, ?)",
                    user.getId(), friend.getId(), true);
        }
    }

    @Override
    public void removeFriend(User user, User friend) {
        SqlRowSet user2Friend = jdbcTemplate.queryForRowSet(
                "select * from FRIENDSHIP where \"user_id\" = ? and \"friend_id\" = ?", user.getId(), friend.getId());
        SqlRowSet friend2User = jdbcTemplate.queryForRowSet(
                "select * from FRIENDSHIP where \"user_id\" = ? and \"friend_id\" = ?", friend.getId(), user.getId());
        if (user2Friend.next()) {
            jdbcTemplate.update("delete from FRIENDSHIP where \"user_id\" =? and \"friend_id\"= ?", user.getId(),
                    friend.getId());
        }

        if (friend2User.next()) {
            jdbcTemplate.update("update FRIENDSHIP set \"status\"= false where \"user_id\"=? and \"friend_id\" =?",
                    friend.getId(), user.getId());
        }
    }

    @Override
    public List<User> getFriends(User user) {
        String sql = "select * from FRIENDSHIP where \"user_id\" = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> getUserById(rs.getInt("friend_id")),
                user.getId());
    }
}
