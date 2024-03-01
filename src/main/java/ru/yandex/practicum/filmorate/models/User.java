package ru.yandex.practicum.filmorate.models;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class User {

    private int id;

    @EqualsAndHashCode.Exclude
    private final Set<Integer> friends = new HashSet<>();

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

    public void addFriend(int userId) {
        friends.add(userId);
    }

    public void deleteFriend(int userId) {
        friends.remove(userId);
    }

    public List<Integer> listFriends() {
        return new ArrayList<>(friends);
    }
}
