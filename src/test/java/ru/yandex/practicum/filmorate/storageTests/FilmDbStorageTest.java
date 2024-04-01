package ru.yandex.practicum.filmorate.storageTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
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
import static org.junit.jupiter.api.Assertions.assertEquals;


@AutoConfigureTestDatabase
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userDbStorage;

    private Film film;
    private User user;

    @BeforeEach
    public void init() {
        film = new Film(0,
                        new ArrayList<>(),
                        "MyFilm",
                        "Просто шедевр",
                        LocalDate.of(1982, 1, 1),
                        1001,
                        new MpaRating(1, "G"),
                        List.of(new Genre(1, "Комедия"), new Genre(2, "Драма")));
        user = new User(1, "aaaa@ggg.ru", "login1", "name1", LocalDate.of(1999, 1, 1));

    }

    @Test
    public void testFindFilmById() {

        filmStorage.addFilm(film);

        Film savedFilm = filmStorage.getFilmByName(film.getName());

        assertThat(savedFilm).isNotNull().usingRecursiveComparison().ignoringFields("id").isEqualTo(film);
    }

    @Test
    public void testGetAllFilms() {

        filmStorage.addFilm(film);
        List<Film> savedFilms = filmStorage.getFilms();

        assertEquals(savedFilms.size(), 1);
    }

    @Test
    public void testUpdateFilm() {

        Film createdFilm = filmStorage.addFilm(film);
        Film updateFilm = new Film(createdFilm.getId(),
                                   new ArrayList<>(),
                                   "MyFilm 2",
                                   "Просто шедевр",
                                   LocalDate.of(1982, 1, 1),
                                   1001,
                                   new MpaRating(1, "G"),
                                   List.of(new Genre(1, "Комедия"), new Genre(2, "Драма")));

        filmStorage.updateFilm(updateFilm);

        Film updatedFilm = filmStorage.getFilmByName(updateFilm.getName());
        assertThat(updatedFilm).isNotNull().usingRecursiveComparison().isEqualTo(updateFilm);
    }

    @Test
    public void testAddLike() {

        userDbStorage.addUser(user);
        Film createdFilm = filmStorage.addFilm(film);

        filmStorage.addLike(createdFilm.getId(), user.getId());

        Film film = filmStorage.getFilmById(createdFilm.getId());
        assertEquals(film.getLikes().size(), 1);
    }

    @Test
    public void testDeleteLike() {

        userDbStorage.addUser(user);

        Film createdFilm = filmStorage.addFilm(film);

        filmStorage.addLike(createdFilm.getId(), user.getId());

        Film likedFilm = filmStorage.getFilmById(createdFilm.getId());
        assertEquals(likedFilm.getLikes().size(), 1);

        filmStorage.removeLike(likedFilm.getId(), user.getId());
        Film dislikedfilm = filmStorage.getFilmById(likedFilm.getId());
        assertEquals(dislikedfilm.getLikes().size(), 0);
    }
}
