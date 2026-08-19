package ru.yandex.practicum.filmorate.mappers;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.FilmReviewDto;
import ru.yandex.practicum.filmorate.model.FilmReview;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FilmReviewMapper {
    FilmReviewDto toDto(FilmReview review);

    FilmReview toEntity(FilmReviewDto reviewDto);

    List<FilmReviewDto> toDto(List<FilmReview> reviews);
}
