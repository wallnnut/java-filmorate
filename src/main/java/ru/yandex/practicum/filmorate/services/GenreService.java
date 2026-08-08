package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.storage.genreStorage.GenreStorage;

import java.util.List;

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
}
