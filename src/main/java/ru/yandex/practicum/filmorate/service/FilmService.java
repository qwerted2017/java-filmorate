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
    private FilmStorage filmStorage;

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

    public void addLike(int userId, int filmId) {
        Film film = filmStorage.getFilmById(filmId);
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(int userId, int filmId) {
        Film film = filmStorage.getFilmById(filmId);
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> listTopTenFilms(int count) {
        return filmStorage.getFilms()
                .stream()
                .sorted(new FilmComparator().reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    static class FilmComparator implements Comparator<Film> {
        public int compare(Film a, Film b) {
            return Integer.compare(a.listLikes().size(), b.listLikes().size());
        }
    }

    public Film addLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film != null) {
            filmStorage.addLike(filmId, userId);
        }

        return film;
    }

    public Film removeLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film != null) {
            filmStorage.removeLike(filmId, userId);
        }

        return film;
    }

    public List<Film> getNFilms(Integer count) {
        return filmStorage.getNFilms(count);
    }
}