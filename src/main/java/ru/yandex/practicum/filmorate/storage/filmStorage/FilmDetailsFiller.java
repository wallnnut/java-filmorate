package ru.yandex.practicum.filmorate.storage.filmStorage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Id;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FilmDetailsFiller {
    private final JdbcTemplate jdbcTemplate;

    public void fill(List<Film> films) {
        fillGenres(films);
        fillDirectors(films);
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

    private void fillDirectors(List<Film> films) {
        if (films == null || films.isEmpty()) {
            return;
        }
        Map<Long, Film> filmsById = new LinkedHashMap<>();
        for (Film film : films) {
            film.setDirectors(new LinkedHashSet<>());
            filmsById.put(film.getId().getId(), film);
        }

        String placeholders = String.join(",", Collections.nCopies(films.size(), "?"));
        String sql = """
                SELECT fd.film_id, d.director_id, d.name
                FROM film_directors fd
                JOIN directors d ON d.director_id = fd.director_id
                WHERE fd.film_id IN (%s)
                """.formatted(placeholders);
        Object[] args = films.stream().map(film -> film.getId().getId()).toArray();
        jdbcTemplate.query(sql, (rs, rowNum) -> {
            long filmId = rs.getLong("film_id");
            Director director = new Director(rs.getLong("director_id"), rs.getString("name"));
            Film film = filmsById.get(filmId);
            if (film != null) {
                film.getDirectors().add(director);
            }
            return null;
        }, args);

        for (Film film : films) {
            Set<Director> sorted = film.getDirectors().stream()
                    .sorted(Comparator.comparing(Director::getId))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            film.setDirectors(sorted);
        }
    }
}
