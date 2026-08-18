package ru.yandex.practicum.filmorate.storage.ratingStorage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmRowMapper;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Primary
@Repository
@RequiredArgsConstructor
public class RatingDbStorage implements RatingStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;

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
    public List<Film> getMostPopular(int count) {
        List<Film> films = jdbcTemplate.query(
                """
                        SELECT f.film_id,
                               f.name,
                               f.description,
                               f.release_date,
                               f.duration,
                               ar.age_rating_id,
                               ar.name AS mpa_name
                        FROM film f
                        JOIN age_rating ar ON ar.age_rating_id = f.age_rating_id
                        LEFT JOIN (
                            SELECT film_id, COUNT(user_id) AS likes_count
                            FROM film_rating
                            GROUP BY film_id
                        ) fr ON f.film_id = fr.film_id
                        ORDER BY COALESCE(fr.likes_count, 0) DESC, f.film_id
                        LIMIT ?
                        """,
                filmRowMapper,
                count
        );
        fillGenres(films);
        return films;
    }

    private void fillGenres(List<Film> films) {
        if (films == null || films.isEmpty()) {
            return;
        }
        Map<Long, Film> filmsById = new LinkedHashMap<>();
        for (Film film : films) {
            film.setGenres(new LinkedHashSet<>());
            filmsById.put(film.getId().getId(), film);
        }

        String placeholders = String.join(",", Collections.nCopies(films.size(), "?"));
        String sql = """
                SELECT fg.film_id, g.genre_id, g.name
                FROM film_genre fg
                JOIN genre g ON g.genre_id = fg.genre_id
                WHERE fg.film_id IN (%s)
                ORDER BY g.genre_id
                """.formatted(placeholders);
        Object[] args = films.stream().map(film -> film.getId().getId()).toArray();

        jdbcTemplate.query(sql, (rs, rowNum) -> {
            long filmId = rs.getLong("film_id");
            Genre genre = new Genre(new Id(rs.getLong("genre_id")), rs.getString("name"));
            Film film = filmsById.get(filmId);
            if (film != null) {
                film.getGenres().add(genre);
            }
            return null;
        }, args);

        for (Film film : films) {
            Set<Genre> sorted = film.getGenres().stream()
                    .sorted(Comparator.comparing(genre -> genre.getId().getId()))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            film.setGenres(sorted);
        }
    }
}
