package ru.yandex.practicum.filmorate.dto;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.model.Operation;

@Data
public class UserEventDto {
    private long timestamp;
    private Id userId;
    private Event eventType;
    private Operation operation;
    private Id eventId;
    private Id entityId;
}
