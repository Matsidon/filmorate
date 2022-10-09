package ru.yandex.practicum.filmorate.model;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private long id;
    private String login;
    private String name;
    private String email;
    private LocalDate birthday;
}
