package ru.yandex.practicum.filmorate.storage.genreStorage;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Id;

import java.util.List;

public interface GenreStorage {
    List<Genre> getAll();

    Genre getById(Id id);
}
