package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.FilmRatingService;
import ru.yandex.practicum.filmorate.services.FilmService;
import ru.yandex.practicum.filmorate.services.FriendShipService;
import ru.yandex.practicum.filmorate.services.UserService;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genreStorage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpaStorage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTest {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final FilmService filmService;
    private final FilmMapper filmMapper;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final FriendShipService friendShipService;
    private final FilmRatingService filmRatingService;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    private User user1;
    private User user2;
    private User user3;
    private Film film1;
    private Film film2;
    private Film film3;

    @BeforeEach
    void init() {
        user1 = newUser("first@test.ru", "first", "User1", LocalDate.of(1982, 10, 8));
        user2 = newUser("second@test.ru", "second", "User2", LocalDate.of(1983, 3, 24));
        user3 = newUser("third@test.ru", "third", "User3", LocalDate.of(2006, 12, 25));

        film1 = newFilm(
                "Film1",
                "description Film1",
                LocalDate.of(1989, 4, 14),
                120,
                new Mpa(new Id(1), "G"),
                Set.of(new Genre(new Id(1), "Комедия"), new Genre(new Id(2), "Драма"))
        );
        film2 = newFilm(
                "Film2",
                "description Film2",
                LocalDate.of(2015, 5, 30),
                108,
                new Mpa(new Id(3), "PG-13"),
                Set.of(new Genre(new Id(6), "Боевик"))
        );
        film3 = newFilm(
                "Film3",
                "description Film3",
                LocalDate.of(1999, 7, 23),
                90,
                new Mpa(new Id(4), "R"),
                Set.of(new Genre(new Id(4), "Триллер"))
        );
    }

    @Test
    void contextLoadsWithInMemoryDatabase() {
        assertTrue(datasourceUrl.startsWith("jdbc:h2:mem:") || datasourceUrl.contains(":h2:mem:"));
        assertThat(genreStorage.getAll()).hasSize(6);
        assertThat(mpaStorage.getAll()).hasSize(5);
    }

    @Test
    void shouldCreateUserAndGetUserById() {
        User created = userStorage.addUser(user1);
        User found = userStorage.getUserById(created.getId());

        assertThat(found)
                .extracting(User::getId, User::getEmail, User::getLogin, User::getName, User::getBirthday)
                .containsExactly(
                        created.getId(),
                        "first@test.ru",
                        "first",
                        "User1",
                        LocalDate.of(1982, 10, 8)
                );
    }

    @Test
    void shouldUseLoginAsNameWhenNameIsBlank() {
        UserDto dto = new UserDto();
        dto.setEmail("blank-name@test.ru");
        dto.setLogin("blankNameLogin");
        dto.setName("   ");
        dto.setBirthday(LocalDate.of(1990, 1, 1));

        UserDto created = userService.addUser(dto);

        assertThat(created.getName()).isEqualTo("blankNameLogin");
        assertThat(userStorage.getUserById(created.getId()).getName()).isEqualTo("blankNameLogin");
    }

    @Test
    void shouldUpdateUser() {
        User created = userStorage.addUser(user1);

        User update = newUser("first@test.ru", "first", "UpdateUser1", LocalDate.of(1982, 10, 8));
        update.setId(created.getId());

        User updated = userStorage.updateUser(update);

        assertThat(updated.getName()).isEqualTo("UpdateUser1");
        assertThat(userStorage.getUserById(created.getId()).getName()).isEqualTo("UpdateUser1");
    }

    @Test
    void shouldDeleteUser() {
        User created = userStorage.addUser(user1);
        userStorage.removeUser(created.getId());

        assertThat(userStorage.getAllUsers()).isEmpty();
        assertThrows(NotFoundException.class, () -> userStorage.getUserById(created.getId()));
    }

    @Test
    void shouldCreateFilmAndGetFilmById() {
        Film created = filmStorage.addFilm(film1);
        Film found = filmStorage.getFilmById(created.getId());

        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getName()).isEqualTo("Film1");
        assertThat(found.getMpa().getId().getId()).isEqualTo(1L);
        assertThat(found.getMpa().getName()).isEqualTo("G");
        assertThat(found.getGenres())
                .extracting(genre -> genre.getId().getId())
                .containsExactly(1L, 2L);
    }

    @Test
    void shouldUpdateFilm() {
        Film created = filmStorage.addFilm(film1);

        Film update = newFilm(
                "Update Name Film1",
                "Update Description Film1",
                LocalDate.of(1989, 4, 14),
                125,
                new Mpa(new Id(2), "PG"),
                Set.of(new Genre(new Id(6), "Боевик"))
        );
        update.setId(created.getId());

        Film updated = filmStorage.updateFilm(update);

        assertThat(updated)
                .hasFieldOrPropertyWithValue("name", "Update Name Film1")
                .hasFieldOrPropertyWithValue("description", "Update Description Film1")
                .hasFieldOrPropertyWithValue("duration", 125);
        assertThat(updated.getMpa().getId().getId()).isEqualTo(2L);
        assertThat(updated.getGenres())
                .extracting(genre -> genre.getId().getId())
                .containsExactly(6L);
    }

    @Test
    void shouldDeleteFilm() {
        Film created = filmStorage.addFilm(film1);
        filmStorage.removeFilm(created.getId());

        assertThat(filmStorage.getAllFilms()).isEmpty();
        assertThrows(NotFoundException.class, () -> filmStorage.getFilmById(created.getId()));
    }

    @Test
    void shouldThrowWhenFilmMpaNotFound() {
        FilmDto dto = filmMapper.toDto(film1);
        dto.setMpa(new Mpa(new Id(999), "UNKNOWN"));
        assertThrows(NotFoundException.class, () -> filmService.addFilm(dto));
    }

    @Test
    void shouldThrowWhenFilmGenreNotFound() {
        FilmDto dto = filmMapper.toDto(film1);
        dto.setGenres(Set.of(new Genre(new Id(999), "UNKNOWN")));
        assertThrows(NotFoundException.class, () -> filmService.addFilm(dto));
    }

    @Test
    void shouldGetAllGenresAndMpa() {
        List<Genre> genres = genreStorage.getAll();
        List<Mpa> mpa = mpaStorage.getAll();

        assertThat(genres)
                .extracting(Genre::getName)
                .containsExactly("Комедия", "Драма", "Мультфильм", "Триллер", "Документальный", "Боевик");
        assertThat(mpa)
                .extracting(Mpa::getName)
                .containsExactly("G", "PG", "PG-13", "R", "NC-17");
        assertThat(genreStorage.getById(new Id(1)).getName()).isEqualTo("Комедия");
        assertThat(mpaStorage.getById(new Id(3)).getName()).isEqualTo("PG-13");
    }

    @Test
    void shouldThrowWhenGenreOrMpaNotFound() {
        assertThrows(NotFoundException.class, () -> genreStorage.getById(new Id(999)));
        assertThrows(NotFoundException.class, () -> mpaStorage.getById(new Id(999)));
    }

    @Test
    void shouldAddAndRemoveLike() {
        User createdUser1 = userStorage.addUser(user1);
        User createdUser2 = userStorage.addUser(user2);
        Film createdFilm = filmStorage.addFilm(film1);

        filmRatingService.putLike(createdFilm.getId(), createdUser1.getId());
        filmRatingService.putLike(createdFilm.getId(), createdUser2.getId());
        filmRatingService.removeLike(createdFilm.getId(), createdUser1.getId());

        List<FilmDto> popular = filmRatingService.getMostPopular(10);
        assertThat(popular)
                .hasSize(1)
                .first()
                .extracting(FilmDto::getId)
                .isEqualTo(createdFilm.getId());
    }

    @Test
    void shouldThrowWhenLikeOrFriendUsesUnknownId() {
        User createdUser = userStorage.addUser(user1);
        Film createdFilm = filmStorage.addFilm(film1);
        Id unknownId = new Id(999L);

        assertThrows(NotFoundException.class,
                () -> filmRatingService.putLike(unknownId, createdUser.getId()));
        assertThrows(NotFoundException.class,
                () -> filmRatingService.putLike(createdFilm.getId(), unknownId));
        assertThrows(NotFoundException.class,
                () -> filmRatingService.removeLike(unknownId, createdUser.getId()));
        assertThrows(NotFoundException.class,
                () -> friendShipService.addFriend(unknownId, createdUser.getId()));
        assertThrows(NotFoundException.class,
                () -> friendShipService.addFriend(createdUser.getId(), unknownId));
        assertThrows(NotFoundException.class,
                () -> friendShipService.getFriends(unknownId));
    }

    @Test
    void shouldGetPopularFilms() {
        User createdUser1 = userStorage.addUser(user1);
        User createdUser2 = userStorage.addUser(user2);
        User createdUser3 = userStorage.addUser(user3);

        Film createdFilm1 = filmStorage.addFilm(film1);
        Film createdFilm2 = filmStorage.addFilm(film2);
        Film createdFilm3 = filmStorage.addFilm(film3);

        filmRatingService.putLike(createdFilm1.getId(), createdUser1.getId());
        filmRatingService.putLike(createdFilm1.getId(), createdUser2.getId());
        filmRatingService.putLike(createdFilm1.getId(), createdUser3.getId());

        filmRatingService.putLike(createdFilm2.getId(), createdUser1.getId());

        filmRatingService.putLike(createdFilm3.getId(), createdUser2.getId());
        filmRatingService.putLike(createdFilm3.getId(), createdUser3.getId());

        List<FilmDto> popular = filmRatingService.getMostPopular(5);
        assertThat(popular)
                .extracting(FilmDto::getName)
                .containsExactly("Film1", "Film3", "Film2");
    }

    @Test
    void shouldAddFriend() {
        User createdUser1 = userStorage.addUser(user1);
        User createdUser2 = userStorage.addUser(user2);

        friendShipService.addFriend(createdUser1.getId(), createdUser2.getId());

        List<UserDto> friends1 = friendShipService.getFriends(createdUser1.getId());
        assertThat(friends1)
                .hasSize(1)
                .extracting(UserDto::getId)
                .containsExactly(createdUser2.getId());
        assertThat(friendShipService.getFriends(createdUser2.getId())).isEmpty();
    }

    @Test
    void shouldRejectFriend() {
        User createdUser1 = userStorage.addUser(user1);
        User createdUser2 = userStorage.addUser(user2);

        friendShipService.addFriend(createdUser1.getId(), createdUser2.getId());
        friendShipService.rejectFriend(createdUser2.getId(), createdUser1.getId());

        assertThat(friendShipService.getFriends(createdUser1.getId())).isEmpty();
        assertThat(friendShipService.getFriends(createdUser2.getId())).isEmpty();
    }

    @Test
    void shouldDeleteFriend() {
        User createdUser1 = userStorage.addUser(user1);
        User createdUser2 = userStorage.addUser(user2);
        User createdUser3 = userStorage.addUser(user3);

        friendShipService.addFriend(createdUser1.getId(), createdUser2.getId());
        friendShipService.addFriend(createdUser1.getId(), createdUser3.getId());
        friendShipService.removeFriend(createdUser1.getId(), createdUser3.getId());

        List<UserDto> friends = friendShipService.getFriends(createdUser1.getId());
        assertThat(friends)
                .hasSize(1)
                .extracting(UserDto::getId)
                .containsExactly(createdUser2.getId());
    }

    @Test
    void shouldGetCommonFriends() {
        User createdUser1 = userStorage.addUser(user1);
        User createdUser2 = userStorage.addUser(user2);
        User createdUser3 = userStorage.addUser(user3);

        friendShipService.addFriend(createdUser1.getId(), createdUser3.getId());
        friendShipService.addFriend(createdUser2.getId(), createdUser3.getId());

        List<UserDto> common = friendShipService.getCommonFriends(createdUser1.getId(), createdUser2.getId());
        assertThat(common)
                .hasSize(1)
                .extracting(UserDto::getId)
                .containsExactly(createdUser3.getId());
    }

    @Test
    void shouldGetCommonFilms() {
        User createdUser1 = userStorage.addUser(user1);
        User createdUser2 = userStorage.addUser(user2);
        Film createdFilm1 = filmStorage.addFilm(film1);
        Film createdFilm2 = filmStorage.addFilm(film2);
        Film createdFilm3 = filmStorage.addFilm(film3);

        filmRatingService.putLike(createdFilm1.getId(), createdUser1.getId());
        filmRatingService.putLike(createdFilm1.getId(), createdUser2.getId());
        filmRatingService.putLike(createdFilm2.getId(), createdUser1.getId());
        filmRatingService.putLike(createdFilm3.getId(), createdUser2.getId());

        List<FilmDto> commonFilms = filmService.getCommonFilms(createdUser1.getId(), createdUser2.getId());
        assertThat(commonFilms)
                .hasSize(1)
                .extracting(FilmDto::getId)
                .containsExactly(createdFilm1.getId());
    }

    @Test
    void shouldSearchFilmsByTitle() {
        Film createdFilm1 = filmStorage.addFilm(film1);
        Film createdFilm2 = filmStorage.addFilm(film2);

        List<FilmDto> result = filmService.searchFilms("Film1", List.of("title"));
        assertThat(result)
                .extracting(FilmDto::getId)
                .containsExactly(createdFilm1.getId());
        assertThat(filmService.searchFilms("Film2", List.of("title")))
                .extracting(FilmDto::getId)
                .containsExactly(createdFilm2.getId());
        assertThat(filmService.searchFilms("Unknown", List.of("title"))).isEmpty();
    }

    @Test
    void shouldSearchFilmsByDirector() {
        List<FilmDto> result = filmService.searchFilms("nonexistent", List.of("director"));
        assertThat(result).isEmpty();
    }

    @Test
    void shouldRecommendFilmsFromSeveralSimilarUsers() {
        User target = userStorage.addUser(user1);
        User similarA = userStorage.addUser(user2);
        User similarB = userStorage.addUser(user3);
        Film shared1 = filmStorage.addFilm(film1);
        Film shared2 = filmStorage.addFilm(film2);
        Film extraFromA = filmStorage.addFilm(film3);
        Film extraFromB = filmStorage.addFilm(newFilm(
                "FilmB",
                "description FilmB",
                LocalDate.of(2001, 1, 1),
                90,
                new Mpa(new Id(1), "G"),
                Set.of()
        ));

        filmRatingService.putLike(shared1.getId(), target.getId());
        filmRatingService.putLike(shared2.getId(), target.getId());

        filmRatingService.putLike(shared1.getId(), similarA.getId());
        filmRatingService.putLike(shared2.getId(), similarA.getId());
        filmRatingService.putLike(extraFromA.getId(), similarA.getId());

        filmRatingService.putLike(shared1.getId(), similarB.getId());
        filmRatingService.putLike(extraFromB.getId(), similarB.getId());

        List<FilmDto> recommendations = filmRatingService.getRecommendations(target.getId());
        assertThat(recommendations)
                .extracting(FilmDto::getId)
                .containsExactlyInAnyOrder(extraFromA.getId(), extraFromB.getId());
    }

    private static User newUser(String email, String login, String name, LocalDate birthday) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(birthday);
        return user;
    }

    private static Film newFilm(String name, String description, LocalDate releaseDate, int duration,
                                Mpa mpa, Set<Genre> genres) {
        Film film = new Film();
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        film.setMpa(mpa);
        film.setGenres(new LinkedHashSet<>(genres));
        return film;
    }
}
