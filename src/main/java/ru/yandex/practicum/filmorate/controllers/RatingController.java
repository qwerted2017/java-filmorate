package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.models.MpaRating;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class RatingController {
    @Autowired
    private RatingService ratingService;

    @GetMapping
    public List<MpaRating> getRatings() {
        return ratingService.getRatings();
    }

    @GetMapping("/{id}")
    public MpaRating getRatingById(@PathVariable("id") Integer ratingId) {
        return ratingService.getRatingById(ratingId);
    }
}
