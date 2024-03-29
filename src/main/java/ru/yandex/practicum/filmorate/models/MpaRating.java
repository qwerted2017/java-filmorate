package ru.yandex.practicum.filmorate.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.criteria.CriteriaBuilder;

@Data
@AllArgsConstructor
public class MpaRating {
    private final Integer rating_id;
    private final String name;
}
