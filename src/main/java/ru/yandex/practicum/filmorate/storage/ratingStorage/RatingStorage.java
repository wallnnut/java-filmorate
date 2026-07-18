package ru.yandex.practicum.filmorate.storage.ratingStorage;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.model.Id;

import java.util.HashSet;
import java.util.List;

public interface RatingStorage {
    public void putLike(Id filmId, Id userId);
    public void removeLike(Id filmId, Id userId);
    public List<Id> getMostPopular(int count);
}
