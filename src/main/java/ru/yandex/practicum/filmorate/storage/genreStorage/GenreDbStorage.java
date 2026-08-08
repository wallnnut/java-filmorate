package ru.yandex.practicum.filmorate.storage.genreStorage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Id;

import java.util.List;

@Primary
@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper genreRowMapper;

    @Override
    public List<Genre> getAll() {
        return jdbcTemplate.query(
                "SELECT genre_id, name FROM genre ORDER BY genre_id",
                genreRowMapper
        );
    }

    @Override
    public Genre getById(Id id) {
        List<Genre> genres = jdbcTemplate.query(
                "SELECT genre_id, name FROM genre WHERE genre_id = ?",
                genreRowMapper,
                id.getId()
        );
        if (genres.isEmpty()) {
            throw new NotFoundException(String.format("Genre with id=%d not found", id.getId()));
        }
        return genres.getFirst();
    }
}
