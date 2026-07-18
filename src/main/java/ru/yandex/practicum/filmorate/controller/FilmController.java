package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
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
    private final FilmMapper filmMapper;
    private final FilmRatingService filmRatingService;

    @PostMapping
    public Film addFilm(@RequestBody @Valid FilmDto film) {
        log.info("Received request to add film: {}", film);
        Film createdFilm = filmService.addFilm(filmMapper.toEntity(film));
        log.info("Film added successfully with id: {}", createdFilm.getId());
        return createdFilm;
    }

    @PutMapping
    public Film updateFilm(@RequestBody FilmDto film) {
        log.info("Received request to update film: {}", film);
        Film updatedFilm = filmService.updateFilm(filmMapper.toEntity(film));
        log.info("Film updated successfully with id: {}", updatedFilm.getId());
        return updatedFilm;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Received request to get all films");
        List<Film> films = filmService.getAllFilms();
        log.info("Returning {} films", films.size());
        return films;
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Id id) {
        log.info("Received request to get film by id: {}", id);
        Film film = filmService.getFilmById(id);
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
    public List<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        log.info("Received request to get popular films with count = {}", count);
        List<Id> popularIds = filmRatingService.getMostPopular(count);
        List<Film> popularFilms = popularIds.stream()
                                            .map(filmService::getFilmById)
                                            .toList();
        log.info("Returning {} popular films", popularFilms.size());
        return popularFilms;
    }
}