package ru.yandex.practicum.filmorate.storage.friendsStorage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.config.FriendshipProperties;
import ru.yandex.practicum.filmorate.model.FriendshipRequestStatus;
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
    private final FriendshipProperties friendshipProperties;

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
        FriendshipRequestStatus status = friendshipProperties.isAutoAccept()
                ? FriendshipRequestStatus.ACCEPTED
                : FriendshipRequestStatus.PENDING;
        jdbcTemplate.update(
                """
                        INSERT INTO friendship_request (initiator_id, receiver_id, created_at, status)
                        VALUES (?, ?, ?, ?)
                        """,
                userId.getId(),
                friendId.getId(),
                Timestamp.from(Instant.now()),
                status.name()
        );
    }

    @Override
    public void acceptFriend(Id userId, Id friendId) {
        jdbcTemplate.update(
                """
                        UPDATE friendship_request
                        SET status = 'ACCEPTED', updated_at = ?
                        WHERE initiator_id = ? AND receiver_id = ? AND status IN ('PENDING', 'ACCEPTED')
                        """,
                Timestamp.from(Instant.now()),
                friendId.getId(),
                userId.getId()
        );
    }

    @Override
    public void rejectFriend(Id userId, Id friendId) {
        jdbcTemplate.update(
                """
                        UPDATE friendship_request
                        SET status = 'REJECTED', updated_at = ?
                        WHERE initiator_id = ? AND receiver_id = ? AND status IN ('PENDING', 'ACCEPTED')
                        """,
                Timestamp.from(Instant.now()),
                friendId.getId(),
                userId.getId()
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
        return friendshipProperties.isBidirectional()
                ? getFriendsBidirectional(userId)
                : getFriendsOutgoing(userId);
    }

    @Override
    public List<User> getCommonFriends(Id userIdA, Id userIdB) {
        return friendshipProperties.isBidirectional()
                ? getCommonFriendsBidirectional(userIdA, userIdB)
                : getCommonFriendsOutgoing(userIdA, userIdB);
    }

    private List<User> getFriendsOutgoing(Id userId) {
        return jdbcTemplate.query(
                """
                        SELECT u.user_id, u.email, u.login, u.name, u.birthday
                        FROM friendship_request fr
                        JOIN users u ON u.user_id = fr.receiver_id
                        WHERE fr.initiator_id = ? AND fr.status = 'ACCEPTED'
                        ORDER BY u.user_id
                        """,
                userRowMapper,
                userId.getId()
        );
    }

    private List<User> getFriendsBidirectional(Id userId) {
        return jdbcTemplate.query(
                """
                        SELECT u.user_id, u.email, u.login, u.name, u.birthday
                        FROM friendship_request fr
                        JOIN users u ON (
                            (fr.initiator_id = ? AND u.user_id = fr.receiver_id)
                            OR
                            (fr.receiver_id = ? AND u.user_id = fr.initiator_id)
                        )
                        WHERE fr.status = 'ACCEPTED'
                          AND (fr.initiator_id = ? OR fr.receiver_id = ?)
                        ORDER BY u.user_id
                        """,
                userRowMapper,
                userId.getId(),
                userId.getId(),
                userId.getId(),
                userId.getId()
        );
    }

    private List<User> getCommonFriendsOutgoing(Id userIdA, Id userIdB) {
        return jdbcTemplate.query(
                """
                        SELECT u.user_id, u.email, u.login, u.name, u.birthday
                        FROM users u
                        WHERE u.user_id IN (
                            SELECT fr.receiver_id FROM friendship_request fr
                            WHERE fr.initiator_id = ? AND fr.status = 'ACCEPTED'
                        )
                        AND u.user_id IN (
                            SELECT fr.receiver_id FROM friendship_request fr
                            WHERE fr.initiator_id = ? AND fr.status = 'ACCEPTED'
                        )
                        ORDER BY u.user_id
                        """,
                userRowMapper,
                userIdA.getId(),
                userIdB.getId()
        );
    }

    private List<User> getCommonFriendsBidirectional(Id userIdA, Id userIdB) {
        return jdbcTemplate.query(
                """
                        SELECT u.user_id, u.email, u.login, u.name, u.birthday
                        FROM users u
                        WHERE u.user_id != ? AND u.user_id != ?
                          AND u.user_id IN (
                            SELECT CASE WHEN fr.initiator_id = ? THEN fr.receiver_id ELSE fr.initiator_id END
                            FROM friendship_request fr
                            WHERE fr.status = 'ACCEPTED'
                              AND ? IN (fr.initiator_id, fr.receiver_id)
                          )
                          AND u.user_id IN (
                            SELECT CASE WHEN fr.initiator_id = ? THEN fr.receiver_id ELSE fr.initiator_id END
                            FROM friendship_request fr
                            WHERE fr.status = 'ACCEPTED'
                              AND ? IN (fr.initiator_id, fr.receiver_id)
                          )
                        ORDER BY u.user_id
                        """,
                userRowMapper,
                userIdA.getId(),
                userIdB.getId(),
                userIdA.getId(),
                userIdA.getId(),
                userIdB.getId(),
                userIdB.getId()
        );
    }
}
