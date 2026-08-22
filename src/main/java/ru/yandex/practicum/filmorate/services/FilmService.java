package ru.yandex.practicum.filmorate.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmStorage;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final FilmMapper filmMapper;
    private final MpaService mpaService;
    private final GenreService genreService;

    public FilmDto addFilm(FilmDto film) {
        log.info("Attempting to add film: {}", film);
        checkMpaAndGenres(film);
        Film added = filmStorage.addFilm(filmMapper.toEntity(film));
        log.info("Film added successfully with id {}: {}", added.getId(), added);
        return filmMapper.toDto(added);
    }

    public FilmDto updateFilm(FilmDto film) {
        log.info("Attempting to update film: {}", film);
        checkMpaAndGenres(film);
        Film updated = filmStorage.updateFilm(filmMapper.toEntity(film));
        log.info("Film updated successfully: {}", updated);
        return filmMapper.toDto(updated);
    }

    public FilmDto removeFilm(Id id) {
        log.info("Attempting to remove film with id {}", id);
        Film removed = filmStorage.removeFilm(id);
        log.info("Film removed successfully: {}", removed);
        return filmMapper.toDto(removed);
    }

    public List<FilmDto> getAllFilms() {
        log.debug("Request to get all films");
        List<Film> films = filmStorage.getAllFilms();
        log.info("Returning {} films", films.size());
        return filmMapper.toDto(films);
    }

    public FilmDto getFilmById(Id id) {
        log.debug("Request to get film by id {}", id);
        Film film = filmStorage.getFilmById(id);
        log.info("Found film by id {}: {}", id, film);
        return filmMapper.toDto(film);
    }

    public List<FilmDto> getCommonFilms(Id userId, Id friendId) {
        log.info("Request to get common films of users {} and {}", userId, friendId);
        List<Film> films = filmStorage.getCommonFilms(userId, friendId);
        log.info("Found {} common films", films.size());
        return filmMapper.toDto(films);
    }

    private void checkMpaAndGenres(FilmDto film) {
        mpaService.getById(film.getMpa().getId());
        List<Id> genreIds = film.getGenres().stream()
                .map(Genre::getId)
                .toList();
        genreService.getByIds(genreIds);
    }
}
