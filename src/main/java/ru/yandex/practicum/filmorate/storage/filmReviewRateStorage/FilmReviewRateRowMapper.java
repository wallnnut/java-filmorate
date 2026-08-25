package ru.yandex.practicum.filmorate.storage.filmReviewRateStorage;


import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmReviewRate;
import ru.yandex.practicum.filmorate.model.Id;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmReviewRateRowMapper implements RowMapper<FilmReviewRate> {
    @Override
    public FilmReviewRate mapRow(ResultSet rs, int rowNum) throws SQLException {
        FilmReviewRate rate = new FilmReviewRate();
        rate.setId(new Id(rs.getLong("film_review_rating_id")));
        rate.setFilmReviewId(new Id(rs.getLong("film_review_id")));
        rate.setUserId(new Id(rs.getLong("user_id")));
        rate.setPositive(rs.getBoolean("is_positive"));
        return rate;
    }
}
