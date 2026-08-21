package ru.yandex.practicum.filmorate.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.storage.directorStorage.DirectorDbStorage;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class DirectorService {
    private final DirectorDbStorage directorStorage;

    public List<Director> getAll() {
        log.debug("Request to get all directors");
        return directorStorage.findAll();
    }

    public Director getById(Id id) {
        log.debug("Request to get director by id {}", id);
        return directorStorage.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Director with id=%d does not exist", id.getId())));
    }

    public Director create(Director director) {
        log.info("Attempting to create director: {}", director);
        return directorStorage.create(director);
    }

    public Director update(Director director) {
        log.info("Attempting to update director: {}", director);
        getById(new Id(director.getId()));
        return directorStorage.update(director);
    }

    public void delete(Id id) {
        log.info("Attempting to delete director with id {}", id);
        getById(id);
        directorStorage.delete(id);
    }
}
