package ru.yandex.practicum.filmorate.models;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.annotations.MinimumDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@Builder(toBuilder = true)
public class Film {

    private int id;

    @EqualsAndHashCode.Exclude
    private List<Integer> likes;

    @NotBlank
    @EqualsAndHashCode.Exclude
    private String name;

    @Length(max = 200)
    @EqualsAndHashCode.Exclude
    private String description;

    @MinimumDate
    @EqualsAndHashCode.Exclude
    private LocalDate releaseDate;

    @Positive
    @EqualsAndHashCode.Exclude
    private int duration;

    @NonNull
    private MpaRating mpa;

    private List<Genre> genres;

}
