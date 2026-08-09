package ru.yandex.practicum.filmorate.storage.friendsStorage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.userStorage.UserRowMapper;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Primary
@Repository
@RequiredArgsConstructor
public class FriendsDbStorage implements FriendsStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

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
    public List<User> getFriends(Id userId) {
        return jdbcTemplate.query(
                """
                        SELECT u.user_id, u.email, u.login, u.name, u.birthday
                        FROM friendship_request fr
                        JOIN users u ON u.user_id = fr.receiver_id
                        WHERE fr.initiator_id = ?
                        ORDER BY u.user_id
                        """,
                userRowMapper,
                userId.getId()
        );
    }

    @Override
    public List<User> getCommonFriends(Id userIdA, Id userIdB) {
        return jdbcTemplate.query(
                """
                        SELECT u.user_id, u.email, u.login, u.name, u.birthday
                        FROM friendship_request f1
                        JOIN friendship_request f2 ON f1.receiver_id = f2.receiver_id
                        JOIN users u ON u.user_id = f1.receiver_id
                        WHERE f1.initiator_id = ? AND f2.initiator_id = ?
                        ORDER BY u.user_id
                        """,
                userRowMapper,
                userIdA.getId(),
                userIdB.getId()
        );
    }
}
