package ru.yandex.practicum.filmorate.models;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {

    private int id;

    @NotBlank
    @EqualsAndHashCode.Exclude
    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")
    private String email;

    @NotBlank
    @Pattern(regexp = "^[^\\s]+$")
    @EqualsAndHashCode.Exclude
    private String login;

    @EqualsAndHashCode.Exclude
    private String name;

    @PastOrPresent
    @EqualsAndHashCode.Exclude
    private LocalDate birthday;

}
