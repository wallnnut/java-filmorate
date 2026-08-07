package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.EnumSet;

@Data
@EqualsAndHashCode(callSuper = true)
public class Film extends BaseEntity {
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private EnumSet<FilmGenre> genres;
    private AgeRating ageRating;
}
