package ru.yandex.practicum.filmorate.models;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private final User vasya = User.builder().id(0).email("Vasiliy@alibabaevich.ru").login("murat").name("Radner").birthday(LocalDate.of(1928, 10, 21)).build();

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();


    @Test
    void shouldCreateUser() {
        Set<ConstraintViolation<User>> violations = validator.validate(vasya);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldNotCreateUserIfLoginIsWrong() {
        String[] logins = {"mu rat", "", " ", null};

        Arrays.stream(logins).forEach(login -> {
            User wrongVasya = vasya
                    .toBuilder()
                    .login(login)
                    .build();

            Set<ConstraintViolation<User>> violations = validator.validate(wrongVasya);

            assertFalse(violations.isEmpty());
        });
    }

    @Test
    void shouldFailCreateUserWithWrongEmail() {
        String[] emails = {"Vasiliy@ alibabaevich.ru", "Vasiliyalibabaevich.ru", ".$Vasiliy@alibabaevich.ru", "Vasiliy@", "@alibabaevich.ru", "", null};

        Arrays.stream(emails).forEach(email -> {
            User wrongVasya = vasya.toBuilder().email(email).build();
            Set<ConstraintViolation<User>> violations = validator.validate(wrongVasya);

            assertFalse(violations.isEmpty());
        });
    }

    @Test
    void shouldFailCreateUserWithWrongBirthday() {
        User wrongVasya = vasya
                .toBuilder()
                .birthday(LocalDate.now().plusMonths(1))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(wrongVasya);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }
}