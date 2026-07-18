package ru.yandex.practicum.filmorate.storage.ratingStorage;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Id;

import java.util.*;

@Component
@AllArgsConstructor
public class InMemoryRatingStorage implements RatingStorage {
    private final Map<Id, Set<Id>> filmLikes = new HashMap<>();

    @Override
    public void putLike(Id filmId, Id userId) {
        filmLikes.computeIfAbsent(filmId, i -> new HashSet<>())
                 .add(userId);
    }

    @Override
    public void removeLike(Id filmId, Id userId) {
        if (!filmLikes.containsKey(filmId)) {
            throw new NotFoundException(String.format("entity with id=%d does not exists", filmId.getId()));
        }
        Set<Id> ids = filmLikes.get(filmId);
        if (ids != null) {
            if (!ids.contains(userId)) {
                throw new NotFoundException(String.format("entity with id=%d does not exists", userId.getId()));
            }
            ids.remove(userId);
            if (ids.isEmpty()) {
                filmLikes.remove(filmId);
            }
        }
    }

    @Override
    public List<Id> getMostPopular(int count) {
        return filmLikes.entrySet()
                        .stream()
                        .sorted((e1, e2) -> Integer.compare(e2.getValue()
                                                              .size(), e1.getValue()
                                                                         .size()))
                        .limit(count)
                        .map(Map.Entry::getKey)
                        .toList();
    }
}
