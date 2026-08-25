package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserEvent extends BaseEntity {
    private Long timestamp;
    private Id userId;
    private Event eventType;
    private Operation operation;
    private Id entityId;
}
