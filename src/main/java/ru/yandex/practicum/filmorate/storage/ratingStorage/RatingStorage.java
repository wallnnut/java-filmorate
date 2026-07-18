package ru.yandex.practicum.filmorate.storage.ratingStorage;

import ru.yandex.practicum.filmorate.model.Id;

import java.util.List;

public interface RatingStorage {
    public void putLike(Id filmId, Id userId);

    public void removeLike(Id filmId, Id userId);

    public List<Id> getMostPopular(int count);
}
