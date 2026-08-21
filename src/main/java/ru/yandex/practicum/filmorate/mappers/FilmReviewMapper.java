package ru.yandex.practicum.filmorate.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.filmorate.dto.FilmReviewDto;
import ru.yandex.practicum.filmorate.model.FilmReview;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FilmReviewMapper {
    @Mapping(source = "id", target = "reviewId")
    @Mapping(source = "rate", target = "useful")
    FilmReviewDto toDto(FilmReview review);

    @Mapping(source = "reviewId", target = "id")
    @Mapping(source = "useful", target = "rate")
    FilmReview toEntity(FilmReviewDto reviewDto);

    List<FilmReviewDto> toDto(List<FilmReview> reviews);
}
