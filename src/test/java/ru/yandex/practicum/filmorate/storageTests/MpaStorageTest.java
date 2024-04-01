package ru.yandex.practicum.filmorate.storageTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MpaStorageTest {
    private final MpaStorage mpaStorage;
    private final FilmDbStorage filmDbStorage;
    private Film film;

    @BeforeEach
    public void init() {
        film = new Film(0,
                        new ArrayList<>(),
                        "MyFilm",
                        "Просто шедевр",
                        LocalDate.of(1982, 1, 1),
                        1001,
                        new MpaRating(1, "G"),
                        List.of(new Genre(1, "Комедия")));
    }

    @Test
    public void getMpaRatingById() {
        filmDbStorage.addFilm(film);
        MpaRating mpaRating = mpaStorage.getRatingById(film.getMpa().getId());
        assertThat(mpaRating.getId()).isNotNull().isEqualTo(film.getMpa().getId());
    }

    @Test
    public void getRatingList() {
        assertThat(mpaStorage.getRatings().size()).isEqualTo(5);
    }
}
