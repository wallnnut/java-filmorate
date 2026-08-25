package ru.yandex.practicum.filmorate.storage.filmReviewRateStorage;

import ru.yandex.practicum.filmorate.model.FilmReviewRate;
import ru.yandex.practicum.filmorate.model.Id;

import java.util.Optional;

public interface FilmReviewRateStorage {

    Optional<FilmReviewRate> findReviewRate(Id reviewId, Id userId);

    void insertReviewRate(Id reviewId, Id userId, boolean isPositive);

    void updateReviewRate(Id reviewId, Id userId, boolean isPositive);

    void removeReviewRate(Id reviewId, Id userId);

    void removeDislike(Id reviewId, Id userId);
}
