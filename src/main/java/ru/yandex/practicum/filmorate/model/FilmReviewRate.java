package ru.yandex.practicum.filmorate.model;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FilmReviewRate extends BaseEntity {
    private Id filmReviewId;
    private Id userId;
    private boolean isPositive;
}
