package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final List<Film> films = new ArrayList<>();
    private static int filmCounter = 0;

    private int countFilmId() {
        return ++filmCounter;
    }

    @Override
    public List<Film> getFilms() {
        return films;
    }

    @Override
    public Film getFilmById(int id) {
        return films.stream().filter(film -> film.getId() == id).findAny().orElseThrow(() -> new NotFoundException("No film found with id " + id));
    }

    @Override
    public Film addFilm(Film film) {
        log.info("Start creating new film {}", film.getName());
        try {
            getFilmById(film.getId());
            String e = String.format("Film with id %s exists", film.getId());
            log.error(e);
            throw new ValidationException(e);
        } catch (NotFoundException e) {
            film.setId(countFilmId());
            films.add(film);
            log.info("Film {} added", film);

            return film;
        }
    }

    @Override
    public void deleteFilm(Film film) {
        films.remove(film);
    }

    @Override
    public Film updateFilm(Film film) {

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
