package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
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
    public List<DirectorDto> getAll() {
        log.info("Received request to get all directors");
        return directorService.getAll();
    }

    @GetMapping("/{id}")
    public DirectorDto getById(@PathVariable Long id) {
        log.info("Received request to get director by id: {}", id);
        return directorService.getById(new Id(id));
    }

    @PostMapping
    public DirectorDto create(@Valid @RequestBody DirectorDto directorDto) {
        log.info("Received request to create director: {}", directorDto);
        return directorService.create(directorDto);
    }

    @PutMapping
    public DirectorDto update(@Valid @RequestBody DirectorDto directorDto) {
        log.info("Received request to update director: {}", directorDto);
        return directorService.update(directorDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Received request to delete director by id: {}", id);
        directorService.delete(new Id(id));
    }
}
