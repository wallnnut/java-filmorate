package ru.yandex.practicum.filmorate.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmReviewDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.FilmReviewMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.FilmReview;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.filmReviewStorage.FilmReviewStorage;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class FilmReviewService {
    private final FilmReviewStorage filmReviewStorage;
    private final FilmReviewMapper reviewMapper;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final UserEventService userEventService;

    public FilmReviewDto addReview(FilmReviewDto reviewDto) {
        userStorage.getUserById(reviewDto.getUserId());
        filmStorage.getFilmById(reviewDto.getFilmId());
        FilmReview added = filmReviewStorage.addFilmReview(reviewMapper.toEntity(reviewDto));
        userEventService.record(added.getUserId(), Event.REVIEW, Operation.ADD, added.getId());
        log.info("Review added successfully with id {}", added.getId());
        return reviewMapper.toDto(added);
    }

    public FilmReviewDto updateReview(FilmReviewDto reviewDto) {
        if (reviewDto.getReviewId() == null) {
            throw new NotFoundException("entity with id=null does not exists");
        }
        FilmReview updated = filmReviewStorage.updateFilmReview(reviewMapper.toEntity(reviewDto));
        userEventService.record(updated.getUserId(), Event.REVIEW, Operation.UPDATE, updated.getId());
        log.info("Review updated successfully with id {}", updated.getId());
        return reviewMapper.toDto(updated);
    }

    public FilmReviewDto getReviewById(Id id) {
        FilmReview review = filmReviewStorage.getFilmReviewById(id);
        return reviewMapper.toDto(review);
    }

    public void removeReview(Id id) {
        FilmReview existing = filmReviewStorage.getFilmReviewById(id);
        filmReviewStorage.removeFilmReview(id);
        userEventService.record(existing.getUserId(), Event.REVIEW, Operation.REMOVE, id);
        log.info("Review removed successfully with id {}", id);
    }

    public List<FilmReviewDto> getReviewsByFilmIdAndCount(Id id, Integer count) {
        List<FilmReview> reviews = filmReviewStorage.getFilmReviewByFilmId(id, count);
        return reviewMapper.toDto(reviews);
    }
}
