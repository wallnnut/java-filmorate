package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.storage.genreStorage.GenreStorage;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public List<Genre> getAll() {
        log.debug("Request to get all genres");
        List<Genre> genres = genreStorage.getAll();
        log.info("Returning {} genres", genres.size());
        return genres;
    }

    public Genre getById(Id id) {
        log.debug("Request to get genre by id {}", id);
        Genre genre = genreStorage.getById(id);
        log.info("Found genre by id {}: {}", id, genre);
        return genre;
    }

    public List<Genre> getByIds(List<Id> ids) {
        if (ids == null || ids.isEmpty()) {
            log.debug("getByIds called with empty list");
            return Collections.emptyList();
        }
        List<Id> uniqueIds = ids.stream().distinct().toList();
        log.debug("Request to get genres by ids {}", uniqueIds);
        List<Genre> genres = genreStorage.getByIds(uniqueIds);
        if (genres.size() != uniqueIds.size()) {
            Set<Long> foundIds = genres.stream()
                    .map(genre -> genre.getId().getId())
                    .collect(Collectors.toSet());
            Id missingId = uniqueIds.stream()
                    .filter(id -> !foundIds.contains(id.getId()))
                    .findFirst()
                    .orElseThrow();
            throw new NotFoundException(String.format("Genre with id=%d not found", missingId.getId()));
        }
        log.info("Returning {} genres for requested ids", genres.size());
        return genres;
    }
}
