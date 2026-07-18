package ru.yandex.practicum.filmorate.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ratingStorage.RatingStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class FilmRatingService {
    private static final int DEFAULT_COUNT = 10;
    private final RatingStorage filmRatingStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public void putLike(Id filmId, Id userId) {
        log.info("Attempting to add like from user {} to film {}", userId, filmId);
        log.debug("Validating existence of film {} and user {}", filmId, userId);
        filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);
        log.debug("Film and user validation passed, storing like");
        filmRatingStorage.putLike(filmId, userId);
        log.info("Like from user {} to film {} successfully added", userId, filmId);
    }

    public void removeLike(Id filmId, Id userId) {
        log.info("Attempting to remove like from user {} on film {}", userId, filmId);
        log.debug("Validating existence of film {} and user {}", filmId, userId);
        filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);
        log.debug("Film and user validation passed, removing like");
        filmRatingStorage.removeLike(filmId, userId);
        log.info("Like from user {} on film {} successfully removed", userId, filmId);
    }

    public List<Id> getMostPopular(int count) {
        log.info("Requesting top {} most popular films", count);
        List<Id> popularIds = filmRatingStorage.getMostPopular(count);
        log.debug("Returning {} popular film IDs", popularIds.size());
        return popularIds;
    }

    public List<Id> getMostPopular() {
        log.debug("Requesting most popular films with default count ({})", DEFAULT_COUNT);
        return getMostPopular(DEFAULT_COUNT);
    }
}