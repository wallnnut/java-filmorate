package ru.yandex.practicum.filmorate.storage.ratingStorage;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmStorage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class InMemoryRatingStorage implements RatingStorage {
    private final FilmStorage filmStorage;
    private final Map<Id, Set<Id>> filmLikes = new HashMap<>();

    @Override
    public void putLike(Id filmId, Id userId) {
        filmLikes.computeIfAbsent(filmId, i -> new HashSet<>())
                .add(userId);
    }

    @Override
    public void removeLike(Id filmId, Id userId) {
        Set<Id> ids = filmLikes.get(filmId);
        if (ids != null) {
            ids.remove(userId);
            if (ids.isEmpty()) {
                filmLikes.remove(filmId);
            }
        }
    }

    @Override
    public List<Film> getMostPopular(int count) {
        return filmLikes.entrySet()
                .stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()))
                .limit(count)
                .map(entry -> filmStorage.getFilmById(entry.getKey()))
                .toList();
    }
}
