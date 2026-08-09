package ru.yandex.practicum.filmorate.storage.filmStorage;

import lombok.AllArgsConstructor;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;

@AllArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {
    private final BaseStorage<Film> filmStorage = new BaseStorage<>();

    @Override
    public Film addFilm(Film film) {
        return filmStorage.push(film);
    }

    @Override
    public Film updateFilm(Film film) throws NotFoundException {
        return filmStorage.edit(film);
    }

    @Override
    public Film removeFilm(Id id) throws NotFoundException {
        return filmStorage.remove(id);
    }

    @Override
    public List<Film> getAllFilms() {
        return filmStorage.getList();
    }

    @Override
    public Film getFilmById(Id id) throws NotFoundException {
        return filmStorage.getItemById(id);
    }
}
