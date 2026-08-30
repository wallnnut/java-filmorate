package ru.yandex.practicum.filmorate.storage.userStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Primary
@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    @Override
    public User addUser(User user) {
        String sql = """
                INSERT INTO users (email, login, name, birthday)
                VALUES (?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        user.setId(new Id(id));
        log.debug("User saved to DB with id {}", id);
        return user;
    }

    @Override
    public User updateUser(User user) {
        getUserById(user.getId());
        String sql = """
                UPDATE users
                SET email = ?, login = ?, name = ?, birthday = ?
                WHERE user_id = ?
                """;
        jdbcTemplate.update(
                sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId().getId()
        );
        log.debug("User updated in DB with id {}", user.getId().getId());
        return user;
    }

    @Override
    public User removeUser(Id id) {
        User user = getUserById(id);
        jdbcTemplate.update("DELETE FROM users WHERE user_id = ?", id.getId());
        log.debug("User removed from DB with id {}", id.getId());
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        String sql = """
                SELECT user_id, email, login, name, birthday
                FROM users
                ORDER BY user_id
                """;
        return jdbcTemplate.query(sql, userRowMapper);
    }

    @Override
    public User getUserById(Id id) {
        String sql = """
                SELECT user_id, email, login, name, birthday
                FROM users
                WHERE user_id = ?
                """;
        List<User> users = jdbcTemplate.query(sql, userRowMapper, id.getId());
        if (users.isEmpty()) {
            throw new NotFoundException(String.format("entity with id=%d does not exists", id.getId()));
        }
        return users.getFirst();
    }

    @Override
    public List<User> getUserByIds(List<Id> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = """
                SELECT user_id, email, login, name, birthday
                FROM users
                WHERE user_id IN (%s)
                ORDER BY user_id
                """.formatted(placeholders);
        Object[] args = ids.stream().map(Id::getId).toArray();
        return jdbcTemplate.query(sql, userRowMapper, args);
    }
}
