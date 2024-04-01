package ru.yandex.practicum.filmorate.storageTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.MpaRating;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest // указываем, о необходимости подготовить бины для работы с БД
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testFindFilmById() {
        // Подготавливаем данные для теста
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);

        Film newFilm = new Film(1,
                                new ArrayList<>(),
                                "MyFilm",
                                "Просто шедевр",
                                LocalDate.of(1982, 1, 1),
                                1001,
                                new MpaRating(1, "G"),
                                List.of(new Genre(1, "Комедия"), new Genre(2, "Драма")));
        filmStorage.addFilm(newFilm);

        // вызываем тестируемый метод
        Film savedFilm = filmStorage.getFilmByName(newFilm.getName());

        // проверяем утверждения
        assertThat(savedFilm).isNotNull() // проверяем, что объект не равен null
                             .usingRecursiveComparison() // проверяем, что значения полей нового
                             .isEqualTo(newFilm);        // и сохраненного пользователя - совпадают
    }

    @Test
    public void testUpdateFilm() {
        // Подготавливаем данные для теста
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);

        Film newFilm = new Film(0,
                                new ArrayList<>(),
                                "MyFilm",
                                "Просто шедевр",
                                LocalDate.of(1982, 1, 1),
                                1001,
                                new MpaRating(1, "G"),
                                List.of(new Genre(1, "Комедия"), new Genre(2, "Драма")));
        filmStorage.addFilm(newFilm);

        Film createdFilm = filmStorage.getFilmByName(newFilm.getName());

        Film updatedFilm = new Film(createdFilm.getId(),
                                    new ArrayList<>(),
                                    "MyFilm 2",
                                    "Просто шедевр",
                                    LocalDate.of(1982, 1, 1),
                                    10016,
                                    new MpaRating(4, "R"),
                                    List.of(new Genre(1, "Комедия"), new Genre(2, "Драма")));
        filmStorage.updateFilm(updatedFilm);

        // вызываем тестируемый метод
        Film savedFilm = filmStorage.getFilmById(updatedFilm.getId());

        // проверяем утверждения
        assertThat(savedFilm).isNotNull() // проверяем, что объект не равен null
                             .usingRecursiveComparison() // проверяем, что значения полей нового
                             .isEqualTo(updatedFilm);        // и сохраненного пользователя - совпадают
    }

    @Test
    public void testGetAllFilms() {
        // Подготавливаем данные для теста
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);

        Film newFilm1 = new Film(1,
                                 new ArrayList<>(),
                                 "MyFilm",
                                 "Просто шедевр",
                                 LocalDate.of(1982, 1, 1),
                                 1001,
                                 new MpaRating(1, "G"),
                                 List.of(new Genre(1, "Комедия"), new Genre(2, "Драма")));
        filmStorage.addFilm(newFilm1);

        Film newFilm2 = new Film(2,
                                 new ArrayList<>(),
                                 "MyFilm 2",
                                 "Просто шедевр 2",
                                 LocalDate.of(1982, 1, 1),
                                 10016,
                                 new MpaRating(4, "R"),
                                 List.of(new Genre(1, "Комедия"), new Genre(2, "Драма")));
        filmStorage.addFilm(newFilm2);

        // вызываем тестируемый метод
        List<Film> savedFilms = filmStorage.getFilms();

        // проверяем утверждения
        assertThat(savedFilms).isNotNull() // проверяем, что объект не равен null
                              .usingRecursiveComparison() // проверяем, что значения полей нового
                              .isEqualTo(List.of(newFilm1, newFilm2));        // и сохраненного пользователя - совпадают
    }

}