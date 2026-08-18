package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class FriendShipRequest extends BaseEntity {
    private final Id initiator;
    private final Id receiver;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private FriendshipRequestStatus status;
}
