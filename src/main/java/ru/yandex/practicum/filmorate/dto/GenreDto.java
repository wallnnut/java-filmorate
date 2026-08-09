package ru.yandex.practicum.filmorate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreDto {
    private Id id;
    private String name;
}
