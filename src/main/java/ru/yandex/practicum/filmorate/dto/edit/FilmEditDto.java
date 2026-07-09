package ru.yandex.practicum.filmorate.dto.edit;


import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import ru.yandex.practicum.filmorate.dto.create.FilmDto;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class FilmEditDto extends FilmDto {
    @NotNull
    private Long id;

}