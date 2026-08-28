package ru.yandex.practicum.filmorate.storage.userEventsStorage;

import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.model.UserEvent;

import java.util.List;

public interface UserEventsStorage {
    void write(UserEvent note);

    List<UserEvent> getFeed(Id userId);
}
