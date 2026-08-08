package ru.yandex.practicum.filmorate.storage.mpaStorage;

import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {
    List<Mpa> getAll();

    Mpa getById(Id id);
}
