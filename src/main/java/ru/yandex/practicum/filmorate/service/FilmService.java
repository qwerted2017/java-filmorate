package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(@Qualifier("Repository") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

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

    public List<Film> listTopTenFilms(int count) {
        return filmStorage.getNFilms(count)
                          .stream()
                          .sorted(new FilmComparator().reversed())
                          .limit(count)
                          .collect(Collectors.toList());
    }

    static class FilmComparator implements Comparator<Film> {
        public int compare(Film a, Film b) {
            return Integer.compare(a.getLikes().size(), b.getLikes().size());
        }
    }

    public void addLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film != null) {
            filmStorage.addLike(filmId, userId);
        }
    }

    public void deleteLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film != null) {
            filmStorage.removeLike(filmId, userId);
        }
    }
}