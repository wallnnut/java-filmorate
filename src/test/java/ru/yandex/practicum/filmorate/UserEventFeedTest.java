package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dto.FilmReviewDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.FilmRatingService;
import ru.yandex.practicum.filmorate.services.FilmReviewService;
import ru.yandex.practicum.filmorate.services.FriendShipService;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@Transactional
class UserEventFeedTest {

    @Autowired
    private UserStorage userStorage;
    @Autowired
    private FilmStorage filmStorage;
    @Autowired
    private FriendShipService friendShipService;
    @Autowired
    private FilmRatingService filmRatingService;
    @Autowired
    private FilmReviewService filmReviewService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    private User user1;
    private User user2;
    private Film film;

    @BeforeEach
    void init() {
        user1 = userStorage.addUser(newUser("feed-user1@test.ru", "feedUser1"));
        user2 = userStorage.addUser(newUser("feed-user2@test.ru", "feedUser2"));
        film = filmStorage.addFilm(newFilm("Feed Film"));
    }

    @Test
    void shouldReturnEmptyFeedForUserWithoutEvents() throws Exception {
        mockMvc.perform(get("/users/{id}/feed", user1.getId().getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnNotFoundForUnknownUserFeed() throws Exception {
        mockMvc.perform(get("/users/{id}/feed", 99999))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRecordFriendAddWithFriendUserIdAsEntityId() throws Exception {
        friendShipService.addFriend(user1.getId(), user2.getId());

        mockMvc.perform(get("/users/{id}/feed", user1.getId().getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].eventType").value("FRIEND"))
                .andExpect(jsonPath("$[0].operation").value("ADD"))
                .andExpect(jsonPath("$[0].userId").value(idValue(user1)))
                .andExpect(jsonPath("$[0].entityId").value(idValue(user2)))
                .andExpect(jsonPath("$[0].eventId").isNumber())
                .andExpect(jsonPath("$[0].timestamp").isNumber());

        mockMvc.perform(get("/users/{id}/feed", user2.getId().getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldRecordLikeReviewAndFriendInChronologicalOrder() throws Exception {
        friendShipService.addFriend(user1.getId(), user2.getId());
        filmRatingService.putLike(film.getId(), user1.getId());
        FilmReviewDto review = filmReviewService.addReview(newReview("Nice film", true));

        String json = mockMvc.perform(get("/users/{id}/feed", user1.getId().getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].eventType").value("FRIEND"))
                .andExpect(jsonPath("$[0].operation").value("ADD"))
                .andExpect(jsonPath("$[0].entityId").value(idValue(user2)))
                .andExpect(jsonPath("$[1].eventType").value("LIKE"))
                .andExpect(jsonPath("$[1].operation").value("ADD"))
                .andExpect(jsonPath("$[1].entityId").value(idValue(film)))
                .andExpect(jsonPath("$[2].eventType").value("REVIEW"))
                .andExpect(jsonPath("$[2].operation").value("ADD"))
                .andExpect(jsonPath("$[2].entityId").value(idValue(review.getReviewId())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(json).contains("\"eventId\":");
        assertThat(json).contains("\"eventType\":");
        assertThat(json).doesNotContain("\"positive\"");
        objectMapper.readTree(json).get(0).get("timestamp").isNumber();
    }

    @Test
    void shouldRecordRemoveAndUpdateOperations() throws Exception {
        friendShipService.addFriend(user1.getId(), user2.getId());
        filmRatingService.putLike(film.getId(), user1.getId());
        FilmReviewDto review = filmReviewService.addReview(newReview("Nice film", true));

        review.setContent("Updated");
        filmReviewService.updateReview(review);
        filmReviewService.removeReview(review.getReviewId());
        filmRatingService.removeLike(film.getId(), user1.getId());
        friendShipService.removeFriend(user1.getId(), user2.getId());

        mockMvc.perform(get("/users/{id}/feed", user1.getId().getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(7))
                .andExpect(jsonPath("$[3].eventType").value("REVIEW"))
                .andExpect(jsonPath("$[3].operation").value("UPDATE"))
                .andExpect(jsonPath("$[3].entityId").value(idValue(review.getReviewId())))
                .andExpect(jsonPath("$[4].eventType").value("REVIEW"))
                .andExpect(jsonPath("$[4].operation").value("REMOVE"))
                .andExpect(jsonPath("$[4].entityId").value(idValue(review.getReviewId())))
                .andExpect(jsonPath("$[5].eventType").value("LIKE"))
                .andExpect(jsonPath("$[5].operation").value("REMOVE"))
                .andExpect(jsonPath("$[5].entityId").value(idValue(film)))
                .andExpect(jsonPath("$[6].eventType").value("FRIEND"))
                .andExpect(jsonPath("$[6].operation").value("REMOVE"))
                .andExpect(jsonPath("$[6].entityId").value(idValue(user2)));
    }

    @Test
    void shouldDeleteReviewViaApiAndKeepRemoveEvent() throws Exception {
        FilmReviewDto created = filmReviewService.addReview(newReview("To delete", true));
        long reviewId = created.getReviewId().getId();

        mockMvc.perform(delete("/reviews/{id}", reviewId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/{id}/feed", user1.getId().getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].operation").value("ADD"))
                .andExpect(jsonPath("$[1].operation").value("REMOVE"))
                .andExpect(jsonPath("$[1].entityId").value(Math.toIntExact(reviewId)));
    }

    private FilmReviewDto newReview(String content, boolean isPositive) {
        FilmReviewDto dto = new FilmReviewDto();
        dto.setUserId(user1.getId());
        dto.setFilmId(film.getId());
        dto.setContent(content);
        dto.setPositive(isPositive);
        return dto;
    }

    private static int idValue(User user) {
        return Math.toIntExact(user.getId().getId());
    }

    private static int idValue(Film film) {
        return Math.toIntExact(film.getId().getId());
    }

    private static int idValue(Id id) {
        return Math.toIntExact(id.getId());
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
