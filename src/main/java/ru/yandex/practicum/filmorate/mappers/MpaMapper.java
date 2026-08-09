package ru.yandex.practicum.filmorate.mappers;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MpaMapper {
    MpaDto toDto(Mpa mpa);

    Mpa toEntity(MpaDto mpaDto);

    List<MpaDto> toDto(List<Mpa> ratings);
}
