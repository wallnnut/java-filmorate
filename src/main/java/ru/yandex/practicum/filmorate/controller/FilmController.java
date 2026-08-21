package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.services.FilmRatingService;
import ru.yandex.practicum.filmorate.services.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;
    private final FilmRatingService filmRatingService;

    @PostMapping
    public FilmDto addFilm(@RequestBody @Valid FilmDto film) {
        log.info("Received request to add film: {}", film);
        FilmDto createdFilm = filmService.addFilm(film);
        log.info("Film added successfully with id: {}", createdFilm.getId());
        return createdFilm;
    }

    @PutMapping
    public FilmDto updateFilm(@RequestBody @Valid FilmDto film) {
        log.info("Received request to update film: {}", film);
        FilmDto updatedFilm = filmService.updateFilm(film);
        log.info("Film updated successfully with id: {}", updatedFilm.getId());
        return updatedFilm;
    }

    @GetMapping
    public List<FilmDto> getAllFilms() {
        log.info("Received request to get all films");
        List<FilmDto> films = filmService.getAllFilms();
        log.info("Returning {} films", films.size());
        return films;
    }

    @GetMapping("/{id}")
    public FilmDto getFilmById(@PathVariable Id id) {
        log.info("Received request to get film by id: {}", id);
        FilmDto film = filmService.getFilmById(id);
        log.info("Found film: {}", film);
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeFilm(@PathVariable Id id, @PathVariable Id userId) {
        log.info("Received request to add like from user {} to film {}", userId, id);
        filmRatingService.putLike(id, userId);
        log.info("Like added successfully from user {} to film {}", userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void unlikeFilm(@PathVariable Id id, @PathVariable Id userId) {
        log.info("Received request to remove like from user {} on film {}", userId, id);
        filmRatingService.removeLike(id, userId);
        log.info("Like removed successfully from user {} on film {}", userId, id);
    }

    @GetMapping("/popular")
    public List<FilmDto> getPopularFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        log.info("Received request to get popular films with count = {}", count);
        List<FilmDto> popularFilms = filmRatingService.getMostPopular(count);
        log.info("Returning {} popular films", popularFilms.size());
        return popularFilms;
    }

    @GetMapping("/director/{directorId}")
    public List<FilmDto> getFilmsByDirector(@PathVariable Id directorId, @RequestParam String sortBy) {
        log.info("Received request to get films by director id: {} sorted by: {}", directorId, sortBy);
        List<FilmDto> films = filmService.getFilmsByDirector(directorId, sortBy);
        log.info("Returning {} films for director id: {}", films.size(), directorId);
        return films;
    }
}
