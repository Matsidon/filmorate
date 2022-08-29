package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private long id;
    private final String login;
    private String name;
    private final String email;
    private final LocalDate birthday;
    private Set<Long> friends;

    public User(String login, String name, String email, LocalDate birthday) {
        this.login = login;
        this.name = name;
        this.email = email;
        this.birthday = birthday;
        this.friends = new HashSet<>();
    }
}
