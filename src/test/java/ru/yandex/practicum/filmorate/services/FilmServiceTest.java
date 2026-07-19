package ru.yandex.practicum.filmorate.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.mappers.FilmMapperImpl;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


class FilmServiceTest {

    private FilmService filmService;

    @BeforeEach
    void setUp() {
        FilmStorage filmStorage = new InMemoryFilmStorageSet();
        FilmMapper filmMapper = new FilmMapperImpl();
        filmService = new FilmService(filmStorage, filmMapper);
    }

    private FilmDto createFilm(String name, String description, LocalDate releaseDate, int duration) {
        FilmDto film = new FilmDto();
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        return film;
    }

    @Test
    void addFilm_shouldSaveAndReturnFilmWithGeneratedId() {
        FilmDto film = createFilm("Inception", "Dream within a dream",
                LocalDate.of(2010, 7, 16), 148);
        FilmDto saved = filmService.addFilm(film);

        assertNotNull(saved.getId());
        assertEquals("Inception", saved.getName());
        assertEquals(1L, saved.getId()
                              .getId());

        FilmDto fromStorage = filmService.getFilmById(new Id(1L));
        assertEquals(saved, fromStorage);
    }

    @Test
    void updateFilm_shouldUpdateExistingFilm() {
        FilmDto original = createFilm("Original", "Desc", LocalDate.now(), 100);
        FilmDto saved = filmService.addFilm(original);

        FilmDto updatedFilm = new FilmDto();
        updatedFilm.setId(saved.getId());
        updatedFilm.setName("Updated");
        updatedFilm.setDescription("New desc");
        updatedFilm.setReleaseDate(LocalDate.of(2020, 1, 1));
        updatedFilm.setDuration(120);

        FilmDto updated = filmService.updateFilm(updatedFilm);

        assertEquals(saved.getId(), updated.getId());
        assertEquals("Updated", updated.getName());
        assertEquals("New desc", updated.getDescription());
        assertEquals(120, updated.getDuration());

        FilmDto fromStorage = filmService.getFilmById(new Id(saved.getId()
                                                                  .getId()));
        assertEquals(updated, fromStorage);
    }

    @Test
    void updateFilm_shouldThrowIfFilmNotFound() {
        FilmDto film = new FilmDto();
        film.setId(new Id(999L));
        film.setName("Ghost");
        film.setDescription("Not exists");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(90);
        assertThrows(RuntimeException.class, () -> filmService.updateFilm(film));
    }

    @Test
    void removeFilm_shouldRemoveAndReturnFilm() {
        FilmDto film = createFilm("To Delete", "Will be removed", LocalDate.now(), 80);
        FilmDto saved = filmService.addFilm(film);

        Id id = new Id(saved.getId()
                            .getId());
        FilmDto removed = filmService.removeFilm(id);

        assertEquals(saved, removed);
        assertThrows(RuntimeException.class, () -> filmService.getFilmById(id));
    }

    @Test
    void removeFilm_shouldThrowIfFilmNotFound() {
        Id id = new Id(999L);
        assertThrows(RuntimeException.class, () -> filmService.removeFilm(id));
    }

    @Test
    void getAllFilms_shouldReturnAllAddedFilms() {
        FilmDto film1 = createFilm("Film A", "A", LocalDate.now(), 100);
        FilmDto film2 = createFilm("Film B", "B", LocalDate.now(), 110);

        filmService.addFilm(film1);
        filmService.addFilm(film2);

        List<FilmDto> all = filmService.getAllFilms();
        assertEquals(2, all.size());
        assertTrue(all.stream()
                      .anyMatch(f -> "Film A".equals(f.getName())));
        assertTrue(all.stream()
                      .anyMatch(f -> "Film B".equals(f.getName())));
    }

    @Test
    void getFilmById_shouldReturnCorrectFilm() {
        FilmDto film = createFilm("Target", "Find me", LocalDate.now(), 95);
        FilmDto saved = filmService.addFilm(film);

        FilmDto found = filmService.getFilmById(new Id(saved.getId()
                                                            .getId()));
        assertEquals(saved, found);
    }

    @Test
    void getFilmById_shouldThrowIfNotFound() {
        Id id = new Id(999L);
        assertThrows(RuntimeException.class, () -> filmService.getFilmById(id));
    }

    @Test
    void addFilm_shouldIncrementIdSequentially() {
        FilmDto f1 = createFilm("One", "", LocalDate.now(), 1);
        FilmDto f2 = createFilm("Two", "", LocalDate.now(), 2);
        FilmDto saved1 = filmService.addFilm(f1);
        FilmDto saved2 = filmService.addFilm(f2);
        assertEquals(1L, saved1.getId()
                               .getId());
        assertEquals(2L, saved2.getId()
                               .getId());
    }

    private static class InMemoryFilmStorageSet implements FilmStorage {
        private final Set<Film> films = new HashSet<>();
        private long nextId = 1;

        @Override
        public Film addFilm(Film film) {
            film.setId(new Id(nextId++));
            films.add(film);
            return film;
        }

        @Override
        public Film updateFilm(Film film) {
            Film existing = films.stream()
                                 .filter(f -> f.getId()
                                               .equals(film.getId()))
                                 .findFirst()
                                 .orElseThrow(() -> new RuntimeException("Film not found"));
            films.remove(existing);
            films.add(film);
            return film;
        }

        @Override
        public Film removeFilm(Id id) {
            Film removed = films.stream()
                                .filter(f -> f.getId()
                                              .equals(id))
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException("Film not found"));
            films.remove(removed);
            return removed;
        }

        @Override
        public List<Film> getAllFilms() {
            return new ArrayList<>(films);
        }

        @Override
        public Film getFilmById(Id id) {
            return films.stream()
                        .filter(f -> f.getId()
                                      .equals(id))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Film not found"));
        }
    }
}