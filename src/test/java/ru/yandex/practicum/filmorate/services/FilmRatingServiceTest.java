package ru.yandex.practicum.filmorate.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.mappers.FilmMapperImpl;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.filmStorage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.ratingStorage.InMemoryRatingStorage;
import ru.yandex.practicum.filmorate.storage.ratingStorage.RatingStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmRatingServiceTest {

    private FilmRatingService filmRatingService;
    private RatingStorage ratingStorage;
    private UserStorage userStorage;
    private FilmStorage filmStorage;

    @BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        ratingStorage = new InMemoryRatingStorage();
        FilmMapper filmMapper = new FilmMapperImpl();
        filmRatingService = new FilmRatingService(ratingStorage, userStorage, filmStorage, filmMapper);
    }

    private Film createFilm(String name, String description, LocalDate releaseDate, int duration) {
        Film film = new Film();
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        return film;
    }

    private User createUser(String email, String login, String name, LocalDate birthday) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(birthday);
        return user;
    }

    @Test
    void putLike_shouldAddLikeForExistingFilmAndUser() {
        Film film = filmStorage.addFilm(createFilm("Film", "Desc", LocalDate.now(), 100));
        User user = userStorage.addUser(createUser("user@mail.ru", "login", "Name", LocalDate.now()));

        filmRatingService.putLike(film.getId(), user.getId());

        List<Id> popular = ratingStorage.getMostPopular(1);
        assertEquals(1, popular.size());
        assertEquals(film.getId(), popular.get(0));
    }

    @Test
    void putLike_shouldThrowIfFilmNotFound() {
        User user = userStorage.addUser(createUser("user@mail.ru", "login", "Name", LocalDate.now()));
        Id fakeFilmId = new Id(999L);

        assertThrows(RuntimeException.class,
                () -> filmRatingService.putLike(fakeFilmId, user.getId()));
    }

    @Test
    void putLike_shouldThrowIfUserNotFound() {
        Film film = filmStorage.addFilm(createFilm("Film", "Desc", LocalDate.now(), 100));
        Id fakeUserId = new Id(999L);

        assertThrows(RuntimeException.class,
                () -> filmRatingService.putLike(film.getId(), fakeUserId));
    }

    @Test
    void removeLike_shouldRemoveExistingLike() {
        Film film = filmStorage.addFilm(createFilm("Film", "Desc", LocalDate.now(), 100));
        User user = userStorage.addUser(createUser("user@mail.ru", "login", "Name", LocalDate.now()));

        filmRatingService.putLike(film.getId(), user.getId());
        filmRatingService.removeLike(film.getId(), user.getId());

        List<FilmDto> popular = filmRatingService.getMostPopular(10);
        System.out.println(popular);
        assertFalse(popular.contains(film.getId()));
    }

    @Test
    void removeLike_shouldThrowIfLikeNotFound() {
        Film film = filmStorage.addFilm(createFilm("Film", "Desc", LocalDate.now(), 100));
        User user = userStorage.addUser(createUser("user@mail.ru", "login", "Name", LocalDate.now()));

        assertThrows(RuntimeException.class,
                () -> filmRatingService.removeLike(film.getId(), user.getId()));
    }

    @Test
    void removeLike_shouldThrowIfFilmNotFound() {
        User user = userStorage.addUser(createUser("user@mail.ru", "login", "Name", LocalDate.now()));
        Id fakeFilmId = new Id(999L);

        assertThrows(RuntimeException.class,
                () -> filmRatingService.removeLike(fakeFilmId, user.getId()));
    }

    @Test
    void removeLike_shouldThrowIfUserNotFound() {
        Film film = filmStorage.addFilm(createFilm("Film", "Desc", LocalDate.now(), 100));
        Id fakeUserId = new Id(999L);

        assertThrows(RuntimeException.class,
                () -> filmRatingService.removeLike(film.getId(), fakeUserId));
    }

    @Test
    void getMostPopular_shouldReturnEmptyListWhenNoLikes() {
        List<FilmDto> top = filmRatingService.getMostPopular(5);
        assertTrue(top.isEmpty());
    }

}