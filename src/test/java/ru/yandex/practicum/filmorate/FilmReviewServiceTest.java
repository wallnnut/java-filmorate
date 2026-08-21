package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dto.FilmReviewDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.FilmReviewService;
import ru.yandex.practicum.filmorate.services.RateFilmReviewService;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class FilmReviewServiceTest {

    @Autowired
    private UserStorage userStorage;
    @Autowired
    private FilmStorage filmStorage;
    @Autowired
    private FilmReviewService filmReviewService;
    @Autowired
    private RateFilmReviewService rateFilmReviewService;
    @Autowired
    private ObjectMapper objectMapper;

    private User user1;
    private User user2;
    private Film film;

    @BeforeEach
    void init() {
        user1 = userStorage.addUser(newUser("review-user1@test.ru", "reviewUser1"));
        user2 = userStorage.addUser(newUser("review-user2@test.ru", "reviewUser2"));
        film = filmStorage.addFilm(newFilm("Review Film"));
    }

    @Test
    void shouldSerializeReviewTypeAsIsPositive() throws Exception {
        FilmReviewDto created = filmReviewService.addReview(newReview("Nice film", true));
        String json = objectMapper.writeValueAsString(created);

        assertThat(json).contains("\"isPositive\":true");
        assertThat(json).contains("\"useful\":0");
        assertThat(json).contains("\"reviewId\":");
        assertThat(json).doesNotContain("\"positive\"");
    }

    @Test
    void shouldCreateReviewWithZeroUseful() {
        FilmReviewDto created = filmReviewService.addReview(newReview("Nice film", true));

        assertThat(created.getReviewId()).isNotNull();
        assertThat(created.getContent()).isEqualTo("Nice film");
        assertThat(created.getPositive()).isTrue();
        assertThat(created.getUseful()).isZero();
        assertThat(created.getUserId()).isEqualTo(user1.getId());
        assertThat(created.getFilmId()).isEqualTo(film.getId());
    }

    @Test
    void shouldUpdateReviewContentAndType() {
        FilmReviewDto created = filmReviewService.addReview(newReview("Nice film", true));
        created.setContent("Changed");
        created.setPositive(false);

        FilmReviewDto updated = filmReviewService.updateReview(created);

        assertThat(updated.getReviewId()).isEqualTo(created.getReviewId());
        assertThat(updated.getContent()).isEqualTo("Changed");
        assertThat(updated.getPositive()).isFalse();
        assertThat(updated.getUserId()).isEqualTo(user1.getId());
        assertThat(updated.getFilmId()).isEqualTo(film.getId());
    }

    @Test
    void shouldChangeUsefulOnLikeDislikeAndRemoval() {
        FilmReviewDto created = filmReviewService.addReview(newReview("Nice film", true));
        Id reviewId = created.getReviewId();

        rateFilmReviewService.like(reviewId, user1.getId());
        assertThat(filmReviewService.getReviewById(reviewId).getUseful()).isEqualTo(1);

        rateFilmReviewService.like(reviewId, user2.getId());
        assertThat(filmReviewService.getReviewById(reviewId).getUseful()).isEqualTo(2);

        rateFilmReviewService.dislike(reviewId, user2.getId());
        assertThat(filmReviewService.getReviewById(reviewId).getUseful()).isZero();

        rateFilmReviewService.removeLike(reviewId, user1.getId());
        assertThat(filmReviewService.getReviewById(reviewId).getUseful()).isEqualTo(-1);

        rateFilmReviewService.removeDislike(reviewId, user2.getId());
        assertThat(filmReviewService.getReviewById(reviewId).getUseful()).isZero();
    }

    @Test
    void shouldSortReviewsByUseful() {
        FilmReviewDto low = filmReviewService.addReview(newReview("Low", true));
        FilmReviewDto high = filmReviewService.addReview(newReview("High", false));

        rateFilmReviewService.dislike(low.getReviewId(), user1.getId());
        rateFilmReviewService.like(high.getReviewId(), user1.getId());
        rateFilmReviewService.like(high.getReviewId(), user2.getId());

        List<FilmReviewDto> reviews = filmReviewService.getReviewsByFilmIdAndCount(film.getId(), 10);

        assertThat(reviews)
                .extracting(FilmReviewDto::getContent)
                .containsExactly("High", "Low");
        assertThat(reviews.get(0).getUseful()).isEqualTo(2);
        assertThat(reviews.get(1).getUseful()).isEqualTo(-1);
    }

    @Test
    void shouldThrowWhenUserOrFilmOrReviewNotFound() {
        Id unknown = new Id(999L);
        FilmReviewDto review = newReview("Nice film", true);
        review.setUserId(unknown);

        assertThrows(NotFoundException.class, () -> filmReviewService.addReview(review));

        review.setUserId(user1.getId());
        review.setFilmId(unknown);
        assertThrows(NotFoundException.class, () -> filmReviewService.addReview(review));

        assertThrows(NotFoundException.class, () -> filmReviewService.getReviewById(unknown));
        assertThrows(NotFoundException.class,
                () -> rateFilmReviewService.like(unknown, user1.getId()));
        assertThrows(NotFoundException.class,
                () -> rateFilmReviewService.like(filmReviewService.addReview(newReview("Ok", true)).getReviewId(),
                        unknown));
    }

    private FilmReviewDto newReview(String content, boolean isPositive) {
        FilmReviewDto dto = new FilmReviewDto();
        dto.setUserId(user1.getId());
        dto.setFilmId(film.getId());
        dto.setContent(content);
        dto.setPositive(isPositive);
        return dto;
    }

    private static User newUser(String email, String login) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName(login);
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }

    private static Film newFilm(String name) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);
        film.setMpa(new Mpa(new Id(1), "G"));
        film.setGenres(new LinkedHashSet<>(Set.of(new Genre(new Id(1), "Комедия"))));
        return film;
    }
}
