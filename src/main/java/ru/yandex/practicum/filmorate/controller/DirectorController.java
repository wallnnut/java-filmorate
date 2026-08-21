package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.services.DirectorService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public List<Director> getAll() {
        log.info("Received request to get all directors");
        return directorService.getAll();
    }

    @GetMapping("/{id}")
    public Director getById(@PathVariable Id id) {
        log.info("Received request to get director by id: {}", id);
        return directorService.getById(id);
    }

    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
        log.info("Received request to create director: {}", director);
        return directorService.create(director);
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        log.info("Received request to update director: {}", director);
        return directorService.update(director);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Id id) {
        log.info("Received request to delete director by id: {}", id);
        directorService.delete(id);
    }
}
