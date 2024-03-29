package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.models.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Service
public class RatingService {
    @Autowired
    MpaStorage mpaStorage;

    public List<MpaRating> getRatings() {
        return mpaStorage.getRatings();
    }

    public MpaRating getRatingById(int ratingId) {
        return mpaStorage.getRatingById(ratingId);
    }
}
