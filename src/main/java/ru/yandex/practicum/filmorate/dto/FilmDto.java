package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.validators.ValidReleaseDate;

import java.time.LocalDate;


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
}