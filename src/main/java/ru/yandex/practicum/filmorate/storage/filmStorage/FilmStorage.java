package ru.yandex.practicum.filmorate.storage.filmStorage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Id;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film removeFilm(Id id);

    List<Film> getAllFilms();

    Film getFilmById(Id id);

    List<Film> getFilmsByDirector(long directorId, String sortBy);

    List<Film> getCommonFilms(Id userId, Id friendId);

    List<Film> searchFilms(String query, List<String> by);
}
