package ru.yandex.practicum.filmorate.storage.friendsStorage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Id;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Primary
@Repository
@RequiredArgsConstructor
public class FriendsDbStorage implements FriendsStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(Id userId, Id friendId) {
        Integer exists = jdbcTemplate.query(
                """
                        SELECT friendship_request_id
                        FROM friendship_request
                        WHERE initiator_id = ? AND receiver_id = ?
                        """,
                rs -> rs.next() ? 1 : null,
                userId.getId(),
                friendId.getId()
        );
        if (exists != null) {
            return;
        }
        jdbcTemplate.update(
                """
                        INSERT INTO friendship_request (initiator_id, receiver_id, created_at, status)
                        VALUES (?, ?, ?, 'PENDING')
                        """,
                userId.getId(),
                friendId.getId(),
                Timestamp.from(Instant.now())
        );
    }

    @Override
    public void removeFriend(Id userId, Id friendId) {
        jdbcTemplate.update(
                """
                        DELETE FROM friendship_request
                        WHERE initiator_id = ? AND receiver_id = ?
                        """,
                userId.getId(),
                friendId.getId()
        );
    }

    @Override
    public List<Id> getFriendIds(Id userId) {
        return jdbcTemplate.query(
                """
                        SELECT receiver_id
                        FROM friendship_request
                        WHERE initiator_id = ?
                        ORDER BY receiver_id
                        """,
                (rs, rowNum) -> new Id(rs.getLong("receiver_id")),
                userId.getId()
        );
    }

    @Override
    public List<Id> getCommonFriends(Id userIdA, Id userIdB) {
        return jdbcTemplate.query(
                """
                        SELECT f1.receiver_id
                        FROM friendship_request f1
                        JOIN friendship_request f2 ON f1.receiver_id = f2.receiver_id
                        WHERE f1.initiator_id = ? AND f2.initiator_id = ?
                        ORDER BY f1.receiver_id
                        """,
                (rs, rowNum) -> new Id(rs.getLong("receiver_id")),
                userIdA.getId(),
                userIdB.getId()
        );
    }
}
