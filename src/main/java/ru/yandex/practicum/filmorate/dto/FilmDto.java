package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.validators.ValidReleaseDate;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class FilmDto {
    private Id id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;

    @NotNull
    @ValidReleaseDate
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность должна быть положительным числом")
    private Integer duration;

    private Set<Genre> genres = new LinkedHashSet<>();

    @NotNull(message = "Рейтинг MPA обязателен")
    private Mpa mpa;
}
