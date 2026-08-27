package ru.yandex.practicum.filmorate.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ratingStorage.RatingStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class FilmRatingService {
    private final RatingStorage filmRatingStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final FilmMapper filmMapper;
    private final UserEventService userEventService;

    public void putLike(Id filmId, Id userId) {
        log.info("Attempting to add like from user {} to film {}", userId, filmId);
        log.debug("Validating existence of film {} and user {}", filmId, userId);
        filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);
        log.debug("Film and user validation passed, storing like");
        filmRatingStorage.putLike(filmId, userId);
        userEventService.record(userId, Event.LIKE, Operation.ADD, filmId);
        log.info("Like from user {} to film {} successfully added", userId, filmId);
    }

    public void removeLike(Id filmId, Id userId) {
        log.info("Attempting to remove like from user {} on film {}", userId, filmId);
        log.debug("Validating existence of film {} and user {}", filmId, userId);
        filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);
        log.debug("Film and user validation passed, removing like");
        filmRatingStorage.removeLike(filmId, userId);
        userEventService.record(userId, Event.LIKE, Operation.REMOVE, filmId);
        log.info("Like from user {} on film {} successfully removed", userId, filmId);
    }

    public List<FilmDto> getMostPopular(int count, Long genreId, Integer year) {
        log.info("Requesting top {} most popular films with genreId: {} and year: {}", count, genreId, year);
        List<Film> popularFilms = filmRatingStorage.getMostPopular(count, genreId, year);
        log.debug("Returning {} popular films", popularFilms.size());
        return filmMapper.toDto(popularFilms);
    }

    public List<FilmDto> getMostPopular(int count) {
        return getMostPopular(count, null, null);
    }

}