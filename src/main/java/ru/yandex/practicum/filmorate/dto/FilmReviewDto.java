package ru.yandex.practicum.filmorate.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Id;

@Data
public class FilmReviewDto {
    private final Id reviewId;
    private final Id userId;
    private final Id filmId;
    @NotBlank(message = "Комментарий не может быть пустым")
    private String content;
    @NotNull
    private boolean isPositive;
    private long useful;

}