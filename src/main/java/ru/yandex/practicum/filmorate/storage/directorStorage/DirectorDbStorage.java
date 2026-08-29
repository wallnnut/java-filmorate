package ru.yandex.practicum.filmorate.storage.directorStorage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Id;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DirectorDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public List<Director> findAll() {
        return jdbcTemplate.query("SELECT * FROM directors", this::mapRowToDirector);
    }

    public Optional<Director> findById(Id id) {
        List<Director> directors = jdbcTemplate.query("SELECT * FROM directors WHERE director_id = ?", this::mapRowToDirector, id.getId());
        return directors.stream().findFirst();
    }

    public Director create(Director director) {
        String sql = "INSERT INTO directors (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"director_id"});
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);
        director.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return director;
    }

    public Director update(Director director) {
        jdbcTemplate.update("UPDATE directors SET name = ? WHERE director_id = ?", director.getName(), director.getId());
        return director;
    }

    public void delete(Id id) {
        jdbcTemplate.update("DELETE FROM directors WHERE director_id = ?", id.getId());
    }

    private Director mapRowToDirector(ResultSet rs, int rowNum) throws SQLException {
        return new Director(rs.getLong("director_id"), rs.getString("name"));
    }
}
