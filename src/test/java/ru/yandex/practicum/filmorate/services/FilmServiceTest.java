package ru.yandex.practicum.filmorate.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        filmService = new FilmService(filmStorage);
    }

    private Film createFilm(String name, String description, LocalDate releaseDate, int duration) {
        Film film = new Film();
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        return film;
    }

    @Test
    void addFilm_shouldSaveAndReturnFilmWithGeneratedId() {
        Film film = createFilm("Inception", "Dream within a dream",
                LocalDate.of(2010, 7, 16), 148);
        Film saved = filmService.addFilm(film);

        assertNotNull(saved.getId());
        assertEquals("Inception", saved.getName());
        assertEquals(1L, saved.getId()
                              .getId());

        Film fromStorage = filmService.getFilmById(new Id(1L));
        assertEquals(saved, fromStorage);
    }

    @Test
    void updateFilm_shouldUpdateExistingFilm() {
        Film original = createFilm("Original", "Desc", LocalDate.now(), 100);
        Film saved = filmService.addFilm(original);

        Film updatedFilm = new Film();
        updatedFilm.setId(saved.getId());
        updatedFilm.setName("Updated");
        updatedFilm.setDescription("New desc");
        updatedFilm.setReleaseDate(LocalDate.of(2020, 1, 1));
        updatedFilm.setDuration(120);

        Film updated = filmService.updateFilm(updatedFilm);

        assertEquals(saved.getId(), updated.getId());
        assertEquals("Updated", updated.getName());
        assertEquals("New desc", updated.getDescription());
        assertEquals(120, updated.getDuration());

        Film fromStorage = filmService.getFilmById(new Id(saved.getId()
                                                               .getId()));
        assertEquals(updated, fromStorage);
    }

    @Test
    void updateFilm_shouldThrowIfFilmNotFound() {
        Film film = new Film();
        film.setId(new Id(999L));
        film.setName("Ghost");
        film.setDescription("Not exists");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(90);
        assertThrows(RuntimeException.class, () -> filmService.updateFilm(film));
    }

    @Test
    void removeFilm_shouldRemoveAndReturnFilm() {
        Film film = createFilm("To Delete", "Will be removed", LocalDate.now(), 80);
        Film saved = filmService.addFilm(film);

        Id id = new Id(saved.getId()
                            .getId());
        Film removed = filmService.removeFilm(id);

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
        Film film1 = createFilm("Film A", "A", LocalDate.now(), 100);
        Film film2 = createFilm("Film B", "B", LocalDate.now(), 110);

        filmService.addFilm(film1);
        filmService.addFilm(film2);

        List<Film> all = filmService.getAllFilms();
        assertEquals(2, all.size());
        assertTrue(all.stream()
                      .anyMatch(f -> "Film A".equals(f.getName())));
        assertTrue(all.stream()
                      .anyMatch(f -> "Film B".equals(f.getName())));
    }

    @Test
    void getFilmById_shouldReturnCorrectFilm() {
        Film film = createFilm("Target", "Find me", LocalDate.now(), 95);
        Film saved = filmService.addFilm(film);

        Film found = filmService.getFilmById(new Id(saved.getId()
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
        Film f1 = createFilm("One", "", LocalDate.now(), 1);
        Film f2 = createFilm("Two", "", LocalDate.now(), 2);
        Film saved1 = filmService.addFilm(f1);
        Film saved2 = filmService.addFilm(f2);
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