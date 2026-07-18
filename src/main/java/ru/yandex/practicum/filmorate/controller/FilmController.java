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
        return filmService.addFilm(filmMapper.toEntity(film));
    }

    @PutMapping
    public Film updateFilm(@RequestBody FilmDto film) {
        return filmService.updateFilm(filmMapper.toEntity(film));
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Id id) {
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeFilm(@PathVariable Id id, @PathVariable Id userId) {
        filmRatingService.putLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void unlikeFilm(@PathVariable Id id, @PathVariable Id userId) {
        filmService.getFilmById(id);
        filmRatingService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        List<Id> popularIds = filmRatingService.getMostPopular(count);
        return popularIds.stream()
                         .map(filmService::getFilmById)
                         .toList();
    }
}
