package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpaStorage.MpaStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    public List<Mpa> getAll() {
        log.debug("Request to get all MPA ratings");
        List<Mpa> ratings = mpaStorage.getAll();
        log.info("Returning {} MPA ratings", ratings.size());
        return ratings;
    }

    public Mpa getById(Id id) {
        log.debug("Request to get MPA by id {}", id);
        Mpa mpa = mpaStorage.getById(id);
        log.info("Found MPA by id {}: {}", id, mpa);
        return mpa;
    }
}
