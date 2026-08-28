package ru.yandex.practicum.filmorate.storage.ratingStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmDetailsFiller;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmRowMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Primary
@Repository
@RequiredArgsConstructor
@Slf4j
public class RatingDbStorage implements RatingStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;
    private final FilmDetailsFiller filmDetailsFiller;

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
    public List<Film> getMostPopular(int count, Long genreId, Integer year) {
        StringBuilder sqlBuilder = new StringBuilder("""
                SELECT f.film_id,
                       f.name,
                       f.description,
                       f.release_date,
                       f.duration,
                       ar.age_rating_id,
                       ar.name AS mpa_name
                FROM film f
                JOIN age_rating ar ON ar.age_rating_id = f.age_rating_id
                """);

        if (genreId != null) {
            sqlBuilder.append(" JOIN film_genre fg ON f.film_id = fg.film_id ");
        }

        sqlBuilder.append("""
                LEFT JOIN (
                    SELECT film_id, COUNT(user_id) AS likes_count
                    FROM film_rating
                    GROUP BY film_id
                ) fr ON f.film_id = fr.film_id
                """);

        List<Object> args = new ArrayList<>();
        boolean hasConditions = false;

        if (genreId != null) {
            sqlBuilder.append(" WHERE fg.genre_id = ? ");
            args.add(genreId);
            hasConditions = true;
        }

        if (year != null) {
            if (hasConditions) {
                sqlBuilder.append(" AND EXTRACT(YEAR FROM f.release_date) = ? ");
            } else {
                sqlBuilder.append(" WHERE EXTRACT(YEAR FROM f.release_date) = ? ");
            }
            args.add(year);
        }

        sqlBuilder.append(" ORDER BY COALESCE(fr.likes_count, 0) DESC, f.film_id LIMIT ? ");
        args.add(count);

        List<Film> films = jdbcTemplate.query(
                sqlBuilder.toString(),
                filmRowMapper,
                args.toArray()
        );

        filmDetailsFiller.fill(films);
        return films;
    }

    @Override
    public List<Film> getRecommendations(Id userId) {
        Long similarUserId = jdbcTemplate.query(
                """
                        SELECT otherLikes.user_id
                        FROM film_rating myLikes
                        JOIN film_rating otherLikes
                            ON myLikes.film_id = otherLikes.film_id
                            AND otherLikes.user_id != myLikes.user_id
                        WHERE myLikes.user_id = ?
                        GROUP BY otherLikes.user_id
                        ORDER BY COUNT(*) DESC
                        LIMIT 10
                        """,
                rs -> rs.next() ? rs.getLong("user_id") : null,
                userId.getId()
        );

        if (similarUserId == null) {
            log.info("Empty list received");
            return Collections.emptyList();
        }

        List<Film> films = jdbcTemplate.query(
                """
                        SELECT f.film_id, f.name, f.description, f.release_date, f.duration,
                               ar.age_rating_id, ar.name AS mpa_name
                        FROM film_rating similarUserLikes
                        JOIN film f ON f.film_id = similarUserLikes.film_id
                        JOIN age_rating ar ON ar.age_rating_id = f.age_rating_id
                        WHERE similarUserLikes.user_id = ?
                          AND similarUserLikes.film_id NOT IN (
                              SELECT film_id FROM film_rating WHERE user_id = ?
                          )
                        """,
                filmRowMapper,
                similarUserId,
                userId.getId()
        );
        filmDetailsFiller.fill(films);
        return films;
    }
}
