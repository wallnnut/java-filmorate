package ru.yandex.practicum.filmorate.storage.mpaStorage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Primary
@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaRowMapper mpaRowMapper;

    @Override
    public List<Mpa> getAll() {
        return jdbcTemplate.query(
                "SELECT age_rating_id, name FROM age_rating ORDER BY age_rating_id",
                mpaRowMapper
        );
    }

    @Override
    public Mpa getById(Id id) {
        List<Mpa> ratings = jdbcTemplate.query(
                "SELECT age_rating_id, name FROM age_rating WHERE age_rating_id = ?",
                mpaRowMapper,
                id.getId()
        );
        if (ratings.isEmpty()) {
            throw new NotFoundException(String.format("MPA with id=%d not found", id.getId()));
        }
        return ratings.getFirst();
    }
}
