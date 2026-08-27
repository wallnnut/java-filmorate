package ru.yandex.practicum.filmorate.storage.userEventsStorage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.model.UserEvent;

import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserEventsDbStorage implements UserEventsStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserEventsRowMapper mapper;

    @Override
    public List<UserEvent> getFeed(Id userId) {
        String sql = """
                SELECT event_id,
                       user_id,
                       event_type,
                       operation,
                       entity_id,
                       event_time
                FROM user_events
                WHERE user_id = ?
                ORDER BY event_time ASC, event_id ASC
                """;
        return jdbcTemplate.query(sql, mapper, userId.getId());
    }

    @Override
    public void write(UserEvent note) {
        String sql = """
                INSERT INTO user_events (user_id, event_type, operation, entity_id, event_time)
                VALUES (?, ?, ?, ?, ?)
                """;
        long eventTime = note.getTimestamp() != null ? note.getTimestamp() : System.currentTimeMillis();
        jdbcTemplate.update(
                sql,
                note.getUserId().getId(),
                note.getEventType().name(),
                note.getOperation().name(),
                note.getEntityId().getId(),
                new Timestamp(eventTime)
        );
    }
}
