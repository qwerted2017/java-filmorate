package ru.yandex.practicum.filmorate.storageTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testFindFilmById() {
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

        Film savedFilm = filmStorage.getFilmByName(newFilm.getName());

        assertThat(savedFilm).isNotNull()
                             .usingRecursiveComparison()
                             .ignoringFields("id")
                             .isEqualTo(newFilm);
    }

    @Test
    public void testUpdateFilm() {
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

        Film savedFilm = filmStorage.getFilmById(updatedFilm.getId());

        assertThat(savedFilm).isNotNull()
                             .usingRecursiveComparison()
                             .isEqualTo(updatedFilm);
    }

    @Test
    public void testGetAllFilms() {

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

        List<Film> savedFilms = filmStorage.getFilms();

        assertThat(savedFilms).isNotNull()
                              .usingRecursiveComparison()
                              .isEqualTo(List.of(newFilm1, newFilm2));
    }
}