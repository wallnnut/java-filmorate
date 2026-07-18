package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;

@Data
public class Id {
    private final long id;

    @JsonCreator
    public Id(long id) {
        this.id = id;
    }

    @JsonValue
    public long getId() {
        return id;
    }
}