package ru.yandex.practicum.filmorate.storage.filmReviewRateStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmReviewRate;
import ru.yandex.practicum.filmorate.model.Id;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class FilmReviewRateDbRateStorage implements FilmReviewRateStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmReviewRateRowMapper reviewRateRowMapper;

    @Override
    public Optional<FilmReviewRate> findReviewRate(Id reviewId, Id userId) {
        String sql = """
                SELECT film_review_rating_id, film_review_id, user_id, is_positive
                FROM film_review_rating
                WHERE film_review_id = ? AND user_id = ?
                """;
        List<FilmReviewRate> rates = jdbcTemplate.query(
                sql,
                reviewRateRowMapper,
                reviewId.getId(),
                userId.getId()
        );
        return rates.stream().findFirst();
    }

    @Override
    public void insertReviewRate(Id reviewId, Id userId, boolean isPositive) {
        String sql = """
                INSERT INTO film_review_rating (film_review_id, user_id, is_positive)
                VALUES (?, ?, ?)
                """;
        jdbcTemplate.update(sql, reviewId.getId(), userId.getId(), isPositive);
    }

    @Override
    public void updateReviewRate(Id reviewId, Id userId, boolean isPositive) {
        String sql = """
                UPDATE film_review_rating
                SET is_positive = ?
                WHERE film_review_id = ? AND user_id = ?
                """;
        jdbcTemplate.update(sql, isPositive, reviewId.getId(), userId.getId());
    }

    @Override
    public void removeReviewRate(Id reviewId, Id userId) {
        String sql = """
                DELETE FROM film_review_rating
                WHERE film_review_id = ? AND user_id = ?
                """;
        jdbcTemplate.update(sql, reviewId.getId(), userId.getId());
    }

    @Override
    public void removeDislike(Id reviewId, Id userId) {
        String sql = """
                DELETE FROM film_review_rating
                WHERE film_review_id = ? AND user_id = ? AND is_positive = FALSE
                """;
        jdbcTemplate.update(sql, reviewId.getId(), userId.getId());
    }
}
