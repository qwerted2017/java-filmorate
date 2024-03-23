package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.models.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> getFilms();

    Film getFilmById(int id);

    Film addFilm(Film film);

    void deleteFilm(Film film);

    Film updateFilm(Film film);
}
