package ru.yandex.practicum.filmorate.storage.filmReviewStorage;


import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmReview;
import ru.yandex.practicum.filmorate.model.Id;

import java.sql.ResultSet;
import java.sql.SQLException;


@Component
public class FilmReviewRowMapper implements RowMapper<FilmReview> {
    @Override
    public FilmReview mapRow(ResultSet rs, int rowNum) throws SQLException {
        FilmReview review = new FilmReview();
        review.setId(new Id(rs.getLong("film_review_id")));
        review.setFilmId(new Id(rs.getLong("film_id")));
        review.setUserId(new Id(rs.getLong("user_id")));
        review.setContent(rs.getString("film_review_content"));
        review.setPositive(rs.getBoolean("is_positive"));
        review.setRate(rs.getLong("rate"));

        return review;
    }
}
