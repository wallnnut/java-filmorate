package ru.yandex.practicum.filmorate.storage.userEventsStorage;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.UserEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class UserEventsRowMapper implements RowMapper<UserEvent> {
    @Override
    public UserEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserEvent event = new UserEvent();
        event.setId(new Id(rs.getLong("event_id")));
        event.setUserId(new Id(rs.getLong("user_id")));
        event.setEntityId(new Id(rs.getLong("entity_id")));
        event.setEventType(Event.valueOf(rs.getString("event_type")));
        event.setOperation(Operation.valueOf(rs.getString("operation")));
        Timestamp eventTime = rs.getTimestamp("event_time");
        event.setTimestamp(eventTime == null ? null : eventTime.getTime());
        return event;
    }
}
