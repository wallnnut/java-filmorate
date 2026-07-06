package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@SuperBuilder
public class BaseEntity {
    private UUID id;
}
