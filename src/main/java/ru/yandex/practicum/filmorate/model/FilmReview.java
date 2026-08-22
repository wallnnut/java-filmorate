package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FilmReview extends BaseEntity {
    private Id userId;
    private Id filmId;
    private boolean isPositive;
    private String content;
    private long rate;

}

