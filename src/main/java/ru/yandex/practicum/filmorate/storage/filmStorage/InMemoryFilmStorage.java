package ru.yandex.practicum.filmorate.storage.filmStorage;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;

@Component
@AllArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {
    BaseStorage<Film> filmStorage;

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
