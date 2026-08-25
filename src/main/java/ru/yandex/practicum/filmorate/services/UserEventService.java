package ru.yandex.practicum.filmorate.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserEventDto;
import ru.yandex.practicum.filmorate.mappers.UserEventMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.UserEvent;
import ru.yandex.practicum.filmorate.storage.userEventsStorage.UserEventsStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserEventService {
    private final UserEventsStorage userEventsStorage;
    private final UserStorage userStorage;
    private final UserEventMapper userEventMapper;

    public List<UserEventDto> getFeed(Id userId) {
        log.info("Request to get feed of user {}", userId);
        userStorage.getUserById(userId);
        List<UserEventDto> feed = userEventMapper.toDto(userEventsStorage.getFeed(userId));
        log.info("Returning {} events for user {}", feed.size(), userId);
        return feed;
    }

    public void record(Id userId, Event eventType, Operation operation, Id entityId) {
        UserEvent event = new UserEvent();
        event.setUserId(userId);
        event.setEventType(eventType);
        event.setOperation(operation);
        event.setEntityId(entityId);
        event.setTimestamp(System.currentTimeMillis());
        userEventsStorage.write(event);
        log.debug("Recorded {} {} for user {} entity {}", eventType, operation, userId, entityId);
    }
}
