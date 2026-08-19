package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FilmReview extends BaseEntity {
    private final boolean isPositive;
    private final Id userId;
    private final Id filmId;
    private String content;
    private long rate;

}

