package ru.yandex.practicum.filmorate.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/films")
@AllArgsConstructor
@Slf4j
public class FilmController {
    private final List<Film> films = new ArrayList<>();
    private static int filmCounter = 0;

    private int countFilmId() {
        return ++filmCounter;
    }

    @GetMapping
    public List<Film> getFilms() {
        return films;
    }

    @PostMapping
    public Film createFilm(@RequestBody @Valid Film film) {
        log.info("Start creating new film {}", film.getName());
        if (films.stream().filter(film1 -> film.getId() == film1.getId()).findFirst().isEmpty()) {
            film.setId(countFilmId());
            films.add(film);
            log.info("Film {} added", film);
        } else {
            String e = String.format("Film with id %s exists", film.getId());
            log.error(e);
            throw new ValidationException(e);
        }

        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {

        if (films.contains(film)) {
            log.debug("Update existing film {}", film.getName());
            int index = films.indexOf(film);
            films.set(index, film);
            return film;
        } else {
            String e = String.format("Film with id %s not exists", film.getId());
            log.error(e);
            throw new NotFoundException(e);
        }
    }
}
