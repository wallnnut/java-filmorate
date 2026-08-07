package ru.yandex.practicum.filmorate.storage.ratingStorage;

import ru.yandex.practicum.filmorate.model.Id;

import java.util.List;

public interface RatingStorage {
    void putLike(Id filmId, Id userId);

    void removeLike(Id filmId, Id userId);

    List<Id> getMostPopular(int count);
}
