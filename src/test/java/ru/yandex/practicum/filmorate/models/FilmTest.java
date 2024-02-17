package ru.yandex.practicum.filmorate.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FilmTest {
    private final Film film = Film.builder()
            .name("Джентельмены удачи")
            .description("Длинный путь к рыбалке на максималках")
            .releaseDate(LocalDate.parse("1971-12-13"))
            .duration(100)
            .build();
    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    @Test
    void shouldCreateFilm() {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldCreateFilmWithMinDate() {
        Film filmMinDate = film
                .toBuilder()
                .releaseDate(LocalDate.parse("1895-12-28"))
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(filmMinDate);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailCreateFilmWithEmptyName() {
        String[] names = {"", " ", "  ", null};

        Arrays.stream(names).forEach(name -> {
            Film wrongFilm = film
                    .toBuilder()
                    .name(name)
                    .build();

            Set<ConstraintViolation<Film>> violations = validator.validate(wrongFilm);

            Assertions.assertFalse(violations.isEmpty());
        });
    }

    @Test
    void shouldFailCreateFilmLongDescription() {
        Film wrongFilm = film
                .toBuilder()
                .description("1".repeat(201))
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(wrongFilm);

        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals(1, violations.size());
    }

    @Test
    void shouldFailCreateFilmBeforeLumierBrothers() {
        Film wrongFilm = film
                .toBuilder()
                .releaseDate(LocalDate.parse("1895-12-27"))
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(wrongFilm);

        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals(1, violations.size());
    }

    @Test
    void shouldFailCreateFilmNegativeDuration() {
        Film wrongFilm = film
                .toBuilder()
                .duration(-1)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(wrongFilm);

        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals(1, violations.size());
    }
}