package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.List;

@Service
public class GenreService {
    @Autowired
    private GenreDbStorage genreDbStorage;

    public List<Genre> getGenres() {
        return genreDbStorage.getGenres();
    }

    public Genre getGenreById(int genreId) {
        return genreDbStorage.getGenreById(genreId);
    }
}
