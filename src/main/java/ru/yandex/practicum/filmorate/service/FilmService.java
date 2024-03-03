package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    @Autowired
    private FilmStorage filmStorage;

    public Film createFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(int filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void addLike(int userId, int filmId) {
        Film film = filmStorage.getFilmById(filmId);
        film.addLike(userId);
    }

    public void deleteLike(int userId, int filmId) {
        Film film = filmStorage.getFilmById(filmId);
        film.deleteLike(userId);
    }

    public List<Film> listTopTenFilms(int count) {
        return filmStorage.getFilms()
                .stream()
                .sorted(new FilmComparator().reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}

class FilmComparator implements Comparator<Film> {
    public int compare(Film a, Film b) {
        return Integer.compare(a.listLikes().size(), b.listLikes().size());
    }
}
