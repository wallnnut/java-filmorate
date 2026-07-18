package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class FilmRating {
    private final Id filmId;
    private final Id userId;
}
