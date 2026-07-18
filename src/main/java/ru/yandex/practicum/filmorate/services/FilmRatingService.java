package ru.yandex.practicum.filmorate.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Id;
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


    public void putLike(Id filmId, Id userId) {
        userStorage.getUserById(userId);
        log.debug("User {} likes film {}", userId, filmId);
        filmRatingStorage.putLike(filmId, userId);
    }

    public void removeLike(Id filmId, Id userId) {
        userStorage.getUserById(userId);
        log.debug("User {} removes like from film {}", userId, filmId);
        filmRatingStorage.removeLike(filmId, userId);
    }

    public List<Id> getMostPopular(int count) {
        log.debug("Requesting top {} popular films", count);
        return filmRatingStorage.getMostPopular(count);
    }

    public List<Id> getMostPopular() {
        return getMostPopular(DEFAULT_COUNT);
    }
}