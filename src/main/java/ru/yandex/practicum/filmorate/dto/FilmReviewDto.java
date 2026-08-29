package ru.yandex.practicum.filmorate.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Id;

@Data
public class FilmReviewDto {
    private Id reviewId;

    @NotNull
    private Id userId;

    @NotNull
    private Id filmId;

    @NotBlank(message = "Комментарий не может быть пустым")
    private String content;

    @NotNull
    @JsonProperty("isPositive")
    private Boolean positive;

    private long useful;
}
