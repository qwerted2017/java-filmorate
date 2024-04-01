package ru.yandex.practicum.filmorate.storageTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@AutoConfigureTestDatabase
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserDbStorageTest {

    private final UserDbStorage userDbStorage;
    private User user1;
    private User user2;

    @BeforeEach
    public void init() {
        user1 = new User(1, "aaaa@ggg.ru", "login1", "name1", LocalDate.of(1999, 1, 1));
        user2 = new User(2, "bbb@ggg.ru", "login2", "name2", LocalDate.of(1999, 1, 1));
    }

    @Test
    public void addUserTest() {
        userDbStorage.addUser(user1);
        assertThat(userDbStorage.getUsers().size()).isNotNull().isEqualTo(1);
    }

    @Test
    public void findUserById() {
        User createdUser = userDbStorage.addUser(user1);
        assertThat(createdUser).isNotNull().usingRecursiveComparison().ignoringFields("id").isEqualTo(user1);
    }

    @Test
    public void updateUserTest() {
        User createdUser = userDbStorage.addUser(user1);
        createdUser.setName("muhaha");
        User updatedUser = userDbStorage.updateUser(createdUser);
        assertThat(userDbStorage.getUserById(createdUser.getId())).usingRecursiveComparison()
                                                                  .isNotNull()
                                                                  .isEqualTo(updatedUser);
    }

    @Test
    public void getUserTest() {
        userDbStorage.addUser(user1);
        userDbStorage.addUser(user2);
        assertThat(userDbStorage.getUsers().size()).isNotNull().isEqualTo(2);
    }

    @Test
    public void deleteUserTest() {
        userDbStorage.addUser(user1);
        assertThat(userDbStorage.getUsers().size()).isEqualTo(1);
        userDbStorage.deleteUser(user1);
        assertTrue(userDbStorage.getUsers().isEmpty());
    }
}
