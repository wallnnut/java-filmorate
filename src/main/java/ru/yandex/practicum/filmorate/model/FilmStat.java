package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class FilmStat {
    private final Id id;
    private int likes;
}
