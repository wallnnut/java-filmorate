package ru.yandex.practicum.filmorate.storage.ratingStorage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Id;

import java.util.List;

@Primary
@Repository
@RequiredArgsConstructor
public class RatingDbStorage implements RatingStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void putLike(Id filmId, Id userId) {
        Integer exists = jdbcTemplate.query(
                "SELECT film_rating_id FROM film_rating WHERE film_id = ? AND user_id = ?",
                rs -> rs.next() ? 1 : null,
                filmId.getId(),
                userId.getId()
        );
        if (exists != null) {
            return;
        }
        jdbcTemplate.update(
                "INSERT INTO film_rating (film_id, user_id) VALUES (?, ?)",
                filmId.getId(),
                userId.getId()
        );
    }

    @Override
    public void removeLike(Id filmId, Id userId) {
        jdbcTemplate.update(
                "DELETE FROM film_rating WHERE film_id = ? AND user_id = ?",
                filmId.getId(),
                userId.getId()
        );
    }

    @Override
    public List<Id> getMostPopular(int count) {
        return jdbcTemplate.query(
                """
                        SELECT f.film_id
                        FROM film f
                        LEFT JOIN film_rating fr ON f.film_id = fr.film_id
                        GROUP BY f.film_id
                        ORDER BY COUNT(fr.user_id) DESC, f.film_id
                        LIMIT ?
                        """,
                (rs, rowNum) -> new Id(rs.getLong("film_id")),
                count
        );
    }
}
