package ru.yandex.practicum.filmorate.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.storage.directorStorage.DirectorDbStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class DirectorService {
    private final DirectorDbStorage directorStorage;

    public List<DirectorDto> getAll() {
        log.debug("Request to get all directors");
        return directorStorage.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public DirectorDto getById(Id id) {
        log.debug("Request to get director by id {}", id);
        Director director = directorStorage.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Director with id=%s does not exist", id.getId())));
        return toDto(director);
    }

    public DirectorDto create(DirectorDto directorDto) {
        log.info("Attempting to create director: {}", directorDto);
        Director director = toEntity(directorDto);
        Director savedDirector = directorStorage.create(director);
        return toDto(savedDirector);
    }

    public DirectorDto update(DirectorDto directorDto) {
        log.info("Attempting to update director: {}", directorDto);
        getById(new Id(directorDto.getId()));
        Director director = toEntity(directorDto);
        Director updatedDirector = directorStorage.update(director);
        return toDto(updatedDirector);
    }

    public void delete(Id id) {
        log.info("Attempting to delete director with id {}", id);
        getById(id);
        directorStorage.delete(id);
    }

    private DirectorDto toDto(Director director) {
        return new DirectorDto(director.getId(), director.getName());
    }

    private Director toEntity(DirectorDto directorDto) {
        return new Director(directorDto.getId(), directorDto.getName());
    }
}
