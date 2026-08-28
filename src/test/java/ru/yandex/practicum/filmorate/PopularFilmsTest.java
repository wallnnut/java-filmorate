package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.DirectorService;
import ru.yandex.practicum.filmorate.services.FilmRatingService;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@Transactional
class PopularFilmsTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserStorage userStorage;
    @Autowired
    private FilmStorage filmStorage;
    @Autowired
    private DirectorService directorService;
    @Autowired
    private FilmRatingService filmRatingService;

    private User user;
    private DirectorDto director;
    private Film filmWithoutDirector;
    private Film filmWithDirector;

    @BeforeEach
    void init() {
        user = userStorage.addUser(newUser("popular@test.ru", "popularUser"));
        director = directorService.create(new DirectorDto(null, "Director updated"));
        filmWithoutDirector = filmStorage.addFilm(newFilm(
                "New film",
                "New film about friends",
                LocalDate.of(1999, 4, 30),
                120,
                Set.of(new Genre(new Id(1), "Комедия"), new Genre(new Id(2), "Драма")),
                Set.of()
        ));
        filmWithDirector = filmStorage.addFilm(newFilm(
                "New film with director",
                "Film with director",
                LocalDate.of(1999, 4, 30),
                120,
                Set.of(new Genre(new Id(1), "Комедия")),
                Set.of(new Director(director.getId(), director.getName()))
        ));
        filmRatingService.putLike(filmWithoutDirector.getId(), user.getId());
    }

    @Test
    void shouldReturnDirectorsInPopularFilmsFilteredByGenre() throws Exception {
        mockMvc.perform(get("/films/popular").param("genreId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(idValue(filmWithoutDirector)))
                .andExpect(jsonPath("$[0].directors.length()").value(0))
                .andExpect(jsonPath("$[1].id").value(idValue(filmWithDirector)))
                .andExpect(jsonPath("$[1].directors.length()").value(1))
                .andExpect(jsonPath("$[1].directors[0].id").value(director.getId().intValue()))
                .andExpect(jsonPath("$[1].directors[0].name").value("Director updated"));
    }

    @Test
    void shouldReturnDirectorsInPopularFilmsFilteredByYearAndGenre() throws Exception {
        mockMvc.perform(get("/films/popular")
                        .param("genreId", "1")
                        .param("year", "1999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].directors.length()").value(0))
                .andExpect(jsonPath("$[1].directors.length()").value(1))
                .andExpect(jsonPath("$[1].directors[0].name").value("Director updated"));
    }

    @Test
    void shouldReturnEmptyPopularListWhenGenreOrYearHasNoFilms() throws Exception {
        mockMvc.perform(get("/films/popular").param("genreId", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        mockMvc.perform(get("/films/popular").param("year", "2000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    private static int idValue(Film film) {
        return Math.toIntExact(film.getId().getId());
    }

    private static User newUser(String email, String login) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName(login);
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }

    private static Film newFilm(String name, String description, LocalDate releaseDate, int duration,
                                Set<Genre> genres, Set<Director> directors) {
        Film film = new Film();
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        film.setMpa(new Mpa(new Id(3), "PG-13"));
        film.setGenres(new LinkedHashSet<>(genres));
        film.setDirectors(new LinkedHashSet<>(directors));
        return film;
    }
}
