package ru.yandex.practicum.filmorate.storage.filmReviewStorage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FilmReview;
import ru.yandex.practicum.filmorate.model.Id;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class FilmReviewDbStorage implements FilmReviewStorage {
    private static final String REVIEW_SELECT = """
            SELECT r.film_review_id,
                   r.film_review_content,
                   r.is_positive,
                   r.user_id,
                   r.film_id,
                   COALESCE((
                       SELECT SUM(CASE WHEN rr.is_positive THEN 1 ELSE -1 END)
                       FROM film_review_rating rr
                       WHERE rr.film_review_id = r.film_review_id
                   ), 0) AS rate
            FROM film_review r
            """;

    private final JdbcTemplate jdbcTemplate;
    private final FilmReviewRowMapper reviewRowMapper;

    @Override
    public FilmReview addFilmReview(FilmReview review) {
        String sql = """
                INSERT INTO film_review (film_review_content, is_positive, user_id, film_id)
                VALUES (?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.isPositive());
            ps.setLong(3, review.getUserId()
                                .getId());
            ps.setLong(4, review.getFilmId()
                                .getId());
            return ps;
        }, keyHolder);
        long id = Objects.requireNonNull(keyHolder.getKey())
                         .longValue();

        return getFilmReviewById(new Id(id));
    }

    @Override
    public FilmReview updateFilmReview(FilmReview review) throws NotFoundException {
        getFilmReviewById(review.getId());
        String sql = """
                UPDATE film_review
                SET film_review_content = ?, is_positive = ?
                WHERE film_review_id = ?
                """;
        jdbcTemplate.update(
                sql,
                review.getContent(),
                review.isPositive(),
                review.getId()
                      .getId()
        );
        return getFilmReviewById(review.getId());
    }

    @Override
    public void removeFilmReview(Id id) throws NotFoundException {
        getFilmReviewById(id);
        String sql = """
                DELETE FROM film_review WHERE film_review_id = ?
                """;
        jdbcTemplate.update(sql, id.getId());
    }

    @Override
    public FilmReview getFilmReviewById(Id id) throws NotFoundException {
        String sql = REVIEW_SELECT + " WHERE r.film_review_id = ?";
        List<FilmReview> filmReview = jdbcTemplate.query(sql, reviewRowMapper, id.getId());

        if (filmReview.isEmpty()) {
            throw new NotFoundException(String.format("entity with id=%d does not exists", id.getId()));
        }

        return filmReview.getFirst();
    }

    @Override
    public List<FilmReview> getFilmReviewByFilmId(Id id, Integer count) throws NotFoundException {
        String sqlWithId = id != null ? " WHERE r.film_id = ?" : "";
        String sqlLimit = count != null ? " LIMIT ?" : "";
        String sql = REVIEW_SELECT + sqlWithId + " ORDER BY rate DESC, r.film_review_id" + sqlLimit;
        List<Object> args = new ArrayList<>();
        if (id != null) {
            args.add(id.getId());
        }
        if (count != null) {
            args.add(count);
        }
        return jdbcTemplate.query(sql, reviewRowMapper, args.toArray());
    }
}
