package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.FilmReviewRate;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.storage.filmReviewRateStorage.FilmReviewRateStorage;
import ru.yandex.practicum.filmorate.storage.filmReviewStorage.FilmReviewStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RateFilmReviewService {
    private final FilmReviewRateStorage rateStorage;
    private final FilmReviewStorage filmReviewStorage;
    private final UserStorage userStorage;

    public void like(Id reviewId, Id userId) {
        putRate(reviewId, userId, true);
    }

    public void dislike(Id reviewId, Id userId) {
        putRate(reviewId, userId, false);
    }

    public void removeLike(Id reviewId, Id userId) {
        ensureReviewAndUserExist(reviewId, userId);
        rateStorage.removeReviewRate(reviewId, userId);
    }

    public void removeDislike(Id reviewId, Id userId) {
        ensureReviewAndUserExist(reviewId, userId);
        rateStorage.removeDislike(reviewId, userId);
    }

    private void putRate(Id reviewId, Id userId, boolean isPositive) {
        ensureReviewAndUserExist(reviewId, userId);
        Optional<FilmReviewRate> existing = rateStorage.findReviewRate(reviewId, userId);
        if (existing.isEmpty()) {
            rateStorage.insertReviewRate(reviewId, userId, isPositive);
            return;
        }
        if (existing.get().isPositive() == isPositive) {
            return;
        }
        rateStorage.updateReviewRate(reviewId, userId, isPositive);
    }

    private void ensureReviewAndUserExist(Id reviewId, Id userId) {
        filmReviewStorage.getFilmReviewById(reviewId);
        userStorage.getUserById(userId);
    }
}
