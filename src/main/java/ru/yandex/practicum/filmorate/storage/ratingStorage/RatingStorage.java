package ru.yandex.practicum.filmorate.storage.ratingStorage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Id;

import java.util.List;

public interface RatingStorage {
    void putLike(Id filmId, Id userId);

    void removeLike(Id filmId, Id userId);

    List<Film> getMostPopular(int count, Long genreId, Integer year);

    default List<Film> getMostPopular(int count) {
        return getMostPopular(count, null, null);
    }
}
