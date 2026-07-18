package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.FilmRatingService;
import ru.yandex.practicum.filmorate.services.FilmService;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.filmStorage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.ratingStorage.RatingStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController filmController;
    private UserStorage userStorage;

    @BeforeEach
    void setUp() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        RatingStorage ratingStorage = new InMemoryRatingStorage();
        FilmMapper filmMapper = new SimpleFilmMapper();

        FilmService filmService = new FilmService(filmStorage);
        FilmRatingService filmRatingService = new FilmRatingService(ratingStorage, userStorage, filmStorage);
        filmController = new FilmController(filmService, filmMapper, filmRatingService);
    }

    private Film createFilm(String name, String description, LocalDate releaseDate, int duration) {
        FilmDto dto = new FilmDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setReleaseDate(releaseDate);
        dto.setDuration(duration);
        return filmController.addFilm(dto);
    }


    private Id createUser(String email, String login, LocalDate birthday) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName(login);
        user.setBirthday(birthday);
        user = userStorage.addUser(user);
        return user.getId();
    }

    @Test
    void addFilm_shouldSaveAndReturnFilm() {
        Film film = createFilm("Inception", "Dream within a dream", LocalDate.of(2010, 7, 16), 148);
        assertNotNull(film.getId());
        assertEquals("Inception", film.getName());
        assertEquals("Dream within a dream", film.getDescription());
        assertEquals(LocalDate.of(2010, 7, 16), film.getReleaseDate());
        assertEquals(148, film.getDuration());

        Film found = filmController.getFilmById(film.getId());
        assertEquals(film, found);
    }

    @Test
    void updateFilm_shouldUpdateExistingFilm() {
        Film created = createFilm("Old", "Old desc", LocalDate.now(), 100);
        FilmDto updateDto = new FilmDto();
        updateDto.setId(created.getId());
        updateDto.setName("New");
        updateDto.setDescription("New desc");
        updateDto.setReleaseDate(LocalDate.of(2020, 1, 1));
        updateDto.setDuration(120);

        Film updated = filmController.updateFilm(updateDto);
        assertEquals(created.getId(), updated.getId());
        assertEquals("New", updated.getName());
        assertEquals("New desc", updated.getDescription());
        assertEquals(LocalDate.of(2020, 1, 1), updated.getReleaseDate());
        assertEquals(120, updated.getDuration());

        Film fromStorage = filmController.getFilmById(created.getId());
        assertEquals(updated, fromStorage);
    }

    @Test
    void updateFilm_shouldThrowIfFilmNotFound() {
        FilmDto dto = new FilmDto();
        dto.setId(new Id(999L));
        dto.setName("Ghost");
        assertThrows(RuntimeException.class, () -> filmController.updateFilm(dto));
    }

    @Test
    void getAllFilms_shouldReturnAll() {
        Film f1 = createFilm("A", "desc1", LocalDate.now(), 1);
        Film f2 = createFilm("B", "desc2", LocalDate.now(), 2);

        List<Film> all = filmController.getAllFilms();
        assertEquals(2, all.size());
        assertTrue(all.contains(f1));
        assertTrue(all.contains(f2));
    }

    @Test
    void getAllFilms_shouldReturnEmptyListWhenNoFilms() {
        assertTrue(filmController.getAllFilms()
                                 .isEmpty());
    }

    @Test
    void getFilmById_shouldReturnFilm() {
        Film created = createFilm("Test", "desc", LocalDate.now(), 90);
        Film found = filmController.getFilmById(created.getId());
        assertEquals(created, found);
    }

    @Test
    void getFilmById_shouldThrowIfNotFound() {
        assertThrows(RuntimeException.class, () -> filmController.getFilmById(new Id(999L)));
    }

    @Test
    void likeFilm_shouldAddLike() {
        Film film = createFilm("Film", "desc", LocalDate.now(), 100);
        Id userId = createUser("user@mail.ru", "login", LocalDate.now());

        filmController.likeFilm(film.getId(), userId);

        List<Film> popular = filmController.getPopularFilms(10);
        assertEquals(1, popular.size());
        assertEquals(film, popular.get(0));
    }

    @Test
    void likeFilm_shouldThrowIfFilmNotFound() {
        Id userId = createUser("user@mail.ru", "login", LocalDate.now());
        assertThrows(RuntimeException.class, () -> filmController.likeFilm(new Id(999L), userId));
    }

    @Test
    void likeFilm_shouldThrowIfUserNotFound() {
        Film film = createFilm("Film", "desc", LocalDate.now(), 100);
        assertThrows(RuntimeException.class, () -> filmController.likeFilm(film.getId(), new Id(999L)));
    }

    @Test
    void unlikeFilm_shouldRemoveLike() {
        Film film = createFilm("Film", "desc", LocalDate.now(), 100);
        Id userId = createUser("user@mail.ru", "login", LocalDate.now());

        filmController.likeFilm(film.getId(), userId);
        assertFalse(filmController.getPopularFilms(10)
                                  .isEmpty());

        filmController.unlikeFilm(film.getId(), userId);
        List<Film> popular = filmController.getPopularFilms(10);
        assertTrue(popular.isEmpty());
    }

    @Test
    void unlikeFilm_shouldDoNothingIfLikeNotExists() {
        Film film = createFilm("Film", "desc", LocalDate.now(), 100);
        Id userId = createUser("user@mail.ru", "login", LocalDate.now());

        filmController.unlikeFilm(film.getId(), userId);
        assertTrue(filmController.getPopularFilms(10)
                                 .isEmpty());
    }

    @Test
    void unlikeFilm_shouldThrowIfFilmNotFound() {
        Id userId = createUser("user@mail.ru", "login", LocalDate.now());
        assertThrows(RuntimeException.class, () -> filmController.unlikeFilm(new Id(999L), userId));
    }

    @Test
    void unlikeFilm_shouldThrowIfUserNotFound() {
        Film film = createFilm("Film", "desc", LocalDate.now(), 100);
        assertThrows(RuntimeException.class, () -> filmController.unlikeFilm(film.getId(), new Id(999L)));
    }

    @Test
    void getPopularFilms_shouldReturnTopFilms() {
        Film f1 = createFilm("F1", "", LocalDate.now(), 100);
        Film f2 = createFilm("F2", "", LocalDate.now(), 120);
        Film f3 = createFilm("F3", "", LocalDate.now(), 90);

        Id u1 = createUser("u1@mail.ru", "l1", LocalDate.now());
        Id u2 = createUser("u2@mail.ru", "l2", LocalDate.now());
        Id u3 = createUser("u3@mail.ru", "l3", LocalDate.now());

        filmController.likeFilm(f1.getId(), u1);
        filmController.likeFilm(f1.getId(), u2);
        filmController.likeFilm(f2.getId(), u3);

        List<Film> top2 = filmController.getPopularFilms(2);
        assertEquals(2, top2.size());
        assertEquals(f1, top2.get(0));
        assertEquals(f2, top2.get(1));

        List<Film> topAll = filmController.getPopularFilms(10);
        assertEquals(2, topAll.size());
        assertEquals(f1, topAll.get(0));
        assertEquals(f2, topAll.get(1));
    }

    @Test
    void getPopularFilms_shouldReturnEmptyListWhenNoLikes() {
        createFilm("F1", "", LocalDate.now(), 100);
        createFilm("F2", "", LocalDate.now(), 120);

        List<Film> popular = filmController.getPopularFilms(5);
        assertTrue(popular.isEmpty());
    }

    @Test
    void getPopularFilms_shouldReturnLimitedCount() {
        Film f1 = createFilm("F1", "", LocalDate.now(), 100);
        Film f2 = createFilm("F2", "", LocalDate.now(), 120);
        Id u1 = createUser("u1@mail.ru", "l1", LocalDate.now());
        Id u2 = createUser("u2@mail.ru", "l2", LocalDate.now());

        filmController.likeFilm(f1.getId(), u1);
        filmController.likeFilm(f2.getId(), u2);

        List<Film> top1 = filmController.getPopularFilms(1);
        assertEquals(1, top1.size());
        assertTrue(top1.contains(f1) || top1.contains(f2));
    }

    private static class SimpleFilmMapper implements FilmMapper {
        @Override
        public Film toEntity(FilmDto dto) {
            if (dto == null) return null;
            Film film = new Film();
            film.setId(dto.getId());
            film.setName(dto.getName());
            film.setDescription(dto.getDescription());
            film.setReleaseDate(dto.getReleaseDate());
            film.setDuration(dto.getDuration());
            return film;
        }

        @Override
        public FilmDto toDto(Film film) {
            if (film == null) return null;
            FilmDto dto = new FilmDto();
            dto.setId(film.getId());
            dto.setName(film.getName());
            dto.setDescription(film.getDescription());
            dto.setReleaseDate(film.getReleaseDate());
            dto.setDuration(film.getDuration());
            return dto;
        }
    }

    private static class InMemoryUserStorage implements UserStorage {
        private final Set<User> users = new HashSet<>();
        private long nextId = 1;

        @Override
        public User addUser(User user) {
            user.setId(new Id(nextId++));
            users.add(user);
            return user;
        }

        @Override
        public User updateUser(User user) {
            User existing = users.stream()
                                 .filter(u -> u.getId()
                                               .equals(user.getId()))
                                 .findFirst()
                                 .orElseThrow(() -> new RuntimeException("User not found"));
            users.remove(existing);
            users.add(user);
            return user;
        }

        @Override
        public User removeUser(Id id) {
            User removed = users.stream()
                                .filter(u -> u.getId()
                                              .equals(id))
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException("User not found"));
            users.remove(removed);
            return removed;
        }

        @Override
        public List<User> getAllUsers() {
            return new ArrayList<>(users);
        }

        @Override
        public User getUserById(Id id) {
            return users.stream()
                        .filter(u -> u.getId()
                                      .equals(id))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("User not found"));
        }

        @Override
        public List<User> getUserByIds(List<Id> ids) {
            List<User> result = new ArrayList<>();
            for (Id id : ids) {
                result.add(getUserById(id));
            }
            return result;
        }
    }

    private static class InMemoryRatingStorage implements RatingStorage {
        private final Map<Id, Set<Id>> filmLikes = new HashMap<>();

        @Override
        public void putLike(Id filmId, Id userId) {
            filmLikes.computeIfAbsent(filmId, k -> new HashSet<>())
                     .add(userId);
        }

        @Override
        public void removeLike(Id filmId, Id userId) {
            Set<Id> ids = filmLikes.get(filmId);
            if (ids != null) {
                ids.remove(userId);
                if (ids.isEmpty()) {
                    filmLikes.remove(filmId);
                }
            }
        }

        @Override
        public List<Id> getMostPopular(int count) {
            return filmLikes.entrySet()
                            .stream()
                            .sorted((e1, e2) -> Integer.compare(e2.getValue()
                                                                  .size(), e1.getValue()
                                                                             .size()))
                            .limit(count)
                            .map(Map.Entry::getKey)
                            .toList();
        }
    }
}