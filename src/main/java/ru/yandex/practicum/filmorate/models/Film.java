package ru.yandex.practicum.filmorate.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.annotations.MinimumDate;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@Builder(toBuilder = true)
public class Film {

    private int id;

    @EqualsAndHashCode.Exclude
    private final Set<Integer> likes = new HashSet<>();

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

    private MpaRating mpaRating;

    public void addLike(int userId) {
        likes.add(userId);
    }

    public void deleteLike(int userId) {
        if (likes.contains(userId)) {
            likes.remove(userId);
        } else {
            throw new NotFoundException("Данный пользователь еще не оценивал этот шедевр по достоинству");
        }
    }

    public List<Integer> listLikes() {
        return new ArrayList<>(likes);
    }
}
