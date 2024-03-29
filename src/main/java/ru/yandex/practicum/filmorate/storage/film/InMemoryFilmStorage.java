package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final List<Film> films = new ArrayList<>();
    private final Map<Integer, Integer> likesFilms = new HashMap<>();
    private final Map<List<Integer>, Integer> likesUsers = new HashMap<>();
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

    @Override
    public void addLike(Integer filmId, Integer userId) {
        likesUsers.put(List.of(userId, filmId), 1);
        likesFilms.put(filmId, likesFilms.getOrDefault(filmId, 0) + 1);
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
        likesUsers.remove(List.of(userId, filmId));

        // если у фильма уже есть лайки , убираем 1
        if (likesFilms.containsKey(filmId)) {
            int likeCount = likesFilms.get(filmId);
            if (likeCount > 1) {
                likesFilms.put(filmId, likeCount - 1);
            }
        }
    }

    @Override
    public List<Film> getNFilms(Integer count) {
        return likesFilms.entrySet()
                .stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .limit(count)
                .map(e -> getFilmById(e.getKey()))
                .collect(Collectors.toList());
    }
}
