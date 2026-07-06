package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.create.film.FilmDto;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.store.Store;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/films")
@SuppressWarnings("unused")
@Slf4j
public class FilmController {

    Store<Film> filmStore = new Store<>();

    @PostMapping
    public Film add(@RequestBody @Valid FilmDto film) {
        UUID id = UUID.randomUUID();
        Film createdFilm = Film.builder().id(id).build();
        BeanUtils.copyProperties(film, createdFilm);
        filmStore.add(createdFilm);
        log.info("Фильм создан: id={}, name={}", createdFilm.getId(), createdFilm.getName());
        return createdFilm;
    }

    @GetMapping
    public List<Film> getFilms() {
        return filmStore.getItems();
    }

    @PutMapping("/{id}")
    public Film edit(@PathVariable UUID id, @RequestBody @Valid FilmDto film) {
        Film foundFilm = filmStore.getItemById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Фильм с ID " + id + " не найден"));
        BeanUtils.copyProperties(film, foundFilm);
        log.info("Фильм обновлён: id={}, name={}", foundFilm.getId(), foundFilm.getName());
        return foundFilm;
    }

}
