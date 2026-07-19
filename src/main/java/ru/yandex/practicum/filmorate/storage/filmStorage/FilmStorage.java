package ru.yandex.practicum.filmorate.storage.filmStorage;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Id;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film) throws NotFoundException;

    Film removeFilm(Id id) throws NotFoundException;

    List<Film> getAllFilms();

    Film getFilmById(Id id) throws NotFoundException;
}
