package ru.yandex.practicum.filmorate.storage.filmStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Id;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Primary
@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;

    private static final String FILM_SELECT = """
            SELECT f.film_id,
                   f.name,
                   f.description,
                   f.release_date,
                   f.duration,
                   ar.age_rating_id,
                   ar.name AS mpa_name
            FROM film f
            JOIN age_rating ar ON ar.age_rating_id = f.age_rating_id
            """;

    @Override
    @Transactional
    public Film addFilm(Film film) {
        String sql = """
                INSERT INTO film (name, description, release_date, duration, age_rating_id)
                VALUES (?, ?, ?, ?, ?)
                """;
        Id mpaId = film.getMpa().getId();
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setLong(5, mpaId.getId());
            return ps;
        }, keyHolder);

        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        saveFilmGenres(id, film.getGenres());
        log.debug("Film saved to DB with id {}", id);
        return getFilmById(new Id(id));
    }

    @Override
    @Transactional
    public Film updateFilm(Film film) throws NotFoundException {
        getFilmById(film.getId());
        String sql = """
                UPDATE film
                SET name = ?, description = ?, release_date = ?, duration = ?, age_rating_id = ?
                WHERE film_id = ?
                """;
        long mpaId = film.getMpa().getId().getId();
        jdbcTemplate.update(
                sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                mpaId,
                film.getId().getId()
        );
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", film.getId().getId());
        saveFilmGenres(film.getId().getId(), film.getGenres());
        log.debug("Film updated in DB with id {}", film.getId().getId());
        return getFilmById(film.getId());
    }

    @Override
    @Transactional
    public Film removeFilm(Id id) throws NotFoundException {
        Film film = getFilmById(id);
        jdbcTemplate.update("DELETE FROM film WHERE film_id = ?", id.getId());
        log.debug("Film removed from DB with id {}", id.getId());
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> films = jdbcTemplate.query(FILM_SELECT + " ORDER BY f.film_id", filmRowMapper);
        fillGenres(films);
        return films;
    }

    @Override
    public Film getFilmById(Id id) throws NotFoundException {
        List<Film> films = jdbcTemplate.query(
                FILM_SELECT + " WHERE f.film_id = ?",
                filmRowMapper,
                id.getId()
        );
        if (films.isEmpty()) {
            throw new NotFoundException(String.format("entity with id=%d does not exists", id.getId()));
        }
        Film film = films.getFirst();
        fillGenres(List.of(film));
        return film;
    }

    @Override
    public List<Film> getCommonFilms(Id userId, Id friendId) {
        List<Film> films = jdbcTemplate.query(
                FILM_SELECT + """
                        WHERE f.film_id IN (
                            SELECT fr1.film_id
                            FROM film_rating fr1
                            JOIN film_rating fr2 ON fr1.film_id = fr2.film_id
                            WHERE fr1.user_id = ? AND fr2.user_id = ?
                        )
                        ORDER BY (
                            SELECT COUNT(*) FROM film_rating fr WHERE fr.film_id = f.film_id
                        ) DESC
                        """,
                filmRowMapper,
                userId.getId(),
                friendId.getId()
        );
        fillGenres(films);
        return films;
    }

    private void saveFilmGenres(long filmId, Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return;
        }
        Set<Long> uniqueGenreIds = genres.stream()
                .filter(Objects::nonNull)
                .map(Genre::getId)
                .filter(Objects::nonNull)
                .map(Id::getId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        for (Long genreId : uniqueGenreIds) {
            jdbcTemplate.update(sql, filmId, genreId);
        }
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
