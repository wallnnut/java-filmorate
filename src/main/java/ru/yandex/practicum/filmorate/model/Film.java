package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class Film extends BaseEntity {
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
}
