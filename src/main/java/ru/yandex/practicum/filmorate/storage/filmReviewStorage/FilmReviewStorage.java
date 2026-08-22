package ru.yandex.practicum.filmorate.storage.filmReviewStorage;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FilmReview;
import ru.yandex.practicum.filmorate.model.Id;

import java.util.List;

public interface FilmReviewStorage {

    FilmReview addFilmReview(FilmReview review);

    FilmReview updateFilmReview(FilmReview review) throws NotFoundException;

    void removeFilmReview(Id id) throws NotFoundException;

    FilmReview getFilmReviewById(Id id) throws NotFoundException;

    List<FilmReview> getFilmReviewByFilmId(Id id, Integer count) throws NotFoundException;


}
