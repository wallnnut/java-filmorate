package ru.yandex.practicum.filmorate.storage.filmStorage;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Id;

import java.util.List;

public interface FilmStorage {
    public Film addFilm(Film film);

    public Film updateFilm(Film film) throws NotFoundException;

    public Film removeFilm(Id id) throws NotFoundException;

    public List<Film> getAllFilms();

    public Film getFilmById(Id id) throws NotFoundException;
}
