package ru.yandex.practicum.filmorate.mappers;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FilmMapper {
    FilmDto toDto(Film film);

    Film toEntity(FilmDto filmDto);

    List<FilmDto> toDto(List<Film> films);
}
