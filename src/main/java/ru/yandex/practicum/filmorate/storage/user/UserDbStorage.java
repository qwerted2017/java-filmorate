package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.User;

import java.sql.Date;
import java.sql.PreparedStatement;
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
                                  (rs, rowNum) -> new User(rs.getInt("user_id"),
                                                           rs.getString("email"),
                                                           rs.getString("login"),
                                                           rs.getString("name"),
                                                           rs.getDate("birthday").toLocalDate()));
    }

    @Override
    public User getUserById(int userId) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("select * from USERS where \"user_id\" = ?", userId);
        if (sqlRowSet.next()) {
            User user = new User(sqlRowSet.getInt("user_id"),
                                 sqlRowSet.getString("email"),
                                 sqlRowSet.getString("login"),
                                 sqlRowSet.getString("name"),
                                 sqlRowSet.getDate("birthday").toLocalDate());
            return user;
        } else {
            String e = String.format("No user found with id %s", userId);
            log.error(e);
            throw new NotFoundException(e);
        }
    }

    @Override
    public User addUser(User user) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("select * from USERS where \"user_id\" = ?", user.getId());
        if (!sqlRowSet.next()) {

            String sql = "insert into USERS (\"email\", \"login\", \"name\", \"birthday\") values (?,?,?,?)";

            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"user_id"});
                stmt.setString(1, user.getEmail());
                stmt.setString(2, user.getLogin());
                stmt.setString(3, user.getName());
                stmt.setDate(4, Date.valueOf(user.getBirthday()));
                return stmt;
            }, keyHolder);

            return getUserById(keyHolder.getKey().intValue());
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
                    "update USERS set \"email\" = ?, \"login\" = ?, \"name\" = ?, \"birthday\" =? where \"user_id\" =" + " ?",
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday(),
                    user.getId());
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
                "select * from FRIENDSHIP where \"user_id\" = ? and \"friend_id\" = ?",
                user.getId(),
                friend.getId());
        SqlRowSet friend2User = jdbcTemplate.queryForRowSet(
                "select * from FRIENDSHIP where \"user_id\" = ? and \"friend_id\" = ?",
                friend.getId(),
                user.getId());

        if (!user2Friend.next()) {
            jdbcTemplate.update("insert into FRIENDSHIP (\"user_id\", \"friend_id\", \"status\") values (?, ?, ?)",
                                user.getId(),
                                friend.getId(),
                                false);
        } else {
            if (friend2User.next()) {
                jdbcTemplate.update("insert into FRIENDSHIP (\"user_id\", \"friend_id\", \"status\") values (?, ?, ?)",
                                    friend.getId(),
                                    user.getId(),
                                    true);
            }

            jdbcTemplate.update("insert into FRIENDSHIP (\"user_id\", \"friend_id\", \"status\") values (?, ?, ?)",
                                user.getId(),
                                friend.getId(),
                                true);
        }
    }

    @Override
    public void removeFriend(User user, User friend) {
        SqlRowSet user2Friend = jdbcTemplate.queryForRowSet(
                "select * from FRIENDSHIP where \"user_id\" = ? and \"friend_id\" = ?",
                user.getId(),
                friend.getId());
        SqlRowSet friend2User = jdbcTemplate.queryForRowSet(
                "select * from FRIENDSHIP where \"user_id\" = ? and \"friend_id\" = ?",
                friend.getId(),
                user.getId());
        if (user2Friend.next()) {
            jdbcTemplate.update("delete from FRIENDSHIP where \"user_id\" =? and \"friend_id\"= ?",
                                user.getId(),
                                friend.getId());
        }

        if (friend2User.next()) {
            jdbcTemplate.update("update FRIENDSHIP set \"status\"= false where \"user_id\"=? and \"friend_id\" =?",
                                friend.getId(),
                                user.getId());
        }
    }

    @Override
    public List<User> getFriends(User user) {
        String sql = "select * from FRIENDSHIP where \"user_id\" = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> getUserById(rs.getInt("friend_id")), user.getId());
    }
}
