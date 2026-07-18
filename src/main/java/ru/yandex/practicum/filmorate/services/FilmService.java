package ru.yandex.practicum.filmorate.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmStorage;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

    public Film addFilm(Film film) {
        log.info("Attempting to add film: {}", film);
        Film added = filmStorage.addFilm(film);
        log.info("Film added successfully with id {}: {}", added.getId(), added);
        return added;
    }

    public Film updateFilm(Film film) {
        log.info("Attempting to update film: {}", film);
        Film updated = filmStorage.updateFilm(film);
        log.info("Film updated successfully: {}", updated);
        return updated;
    }

    public Film removeFilm(Id id) {
        log.info("Attempting to remove film with id {}", id);
        Film removed = filmStorage.removeFilm(id);
        log.info("Film removed successfully: {}", removed);
        return removed;
    }

    public List<Film> getAllFilms() {
        log.debug("Request to get all films");
        List<Film> films = filmStorage.getAllFilms();
        log.info("Returning {} films", films.size());
        return films;
    }

    public Film getFilmById(Id id) {
        log.debug("Request to get film by id {}", id);
        Film film = filmStorage.getFilmById(id);
        log.info("Found film by id {}: {}", id, film);
        return film;
    }
}