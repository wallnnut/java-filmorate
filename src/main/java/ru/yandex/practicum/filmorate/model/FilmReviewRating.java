package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FilmReviewRating extends BaseEntity {
    private final Id filmReviewId;
    private final Id userId;
    private final boolean isPositive;

}