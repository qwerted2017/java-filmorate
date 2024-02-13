package ru.yandex.practicum.filmorate.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Data
@EqualsAndHashCode
public class User {
    private int id;
    @Email
    @NonNull
    @EqualsAndHashCode.Exclude
    private String email;
    @NonNull
    @NotEmpty
    @EqualsAndHashCode.Exclude
    private String login;
    @EqualsAndHashCode.Exclude
    private String name;
    @PastOrPresent
    @EqualsAndHashCode.Exclude
    private LocalDate birthday;

}
