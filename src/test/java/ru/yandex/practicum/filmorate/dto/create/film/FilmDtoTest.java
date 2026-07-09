package ru.yandex.practicum.filmorate.dto.create.film;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.create.FilmDto;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class FilmDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldValidateCorrectFilm() {
        FilmDto film = FilmDto.builder()
                              .build();
        film.setName("Inception");
        film.setDescription("A thief who steals corporate secrets through dream-sharing technology.");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(film);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldPassWhenDescriptionExactly200Chars() {
        FilmDto film = FilmDto.builder()
                              .build();
        film.setName("Film");
        film.setDescription("a".repeat(200));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(film);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldPassWhenReleaseDateExactly1895_12_28() {
        FilmDto film = FilmDto.builder()
                              .build();
        film.setName("Film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(100);

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(film);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldPassWhenReleaseDateAfter1895_12_28() {
        FilmDto film = FilmDto.builder()
                              .build();
        film.setName("Film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(1895, 12, 29));
        film.setDuration(100);

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(film);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldPassWhenDurationIsPositive() {
        FilmDto film = FilmDto.builder()
                              .build();
        film.setName("Film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(1);

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(film);
        assertThat(violations).isEmpty();
    }


    @Test
    void shouldFailWhenNameIsBlank() {
        FilmDto film = FilmDto.builder()
                              .build();
        film.setName("");   // пустая строка
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(film);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator()
                             .next()
                             .getMessage())
                .isEqualTo("Название не может быть пустым");
    }

    @Test
    void shouldFailWhenNameIsNull() {
        FilmDto film = FilmDto.builder()
                              .build();
        film.setName(null);
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(film);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator()
                             .next()
                             .getMessage())
                .isEqualTo("Название не может быть пустым");
    }

    @Test
    void shouldFailWhenDescriptionExceedsMaxLength() {
        FilmDto film = FilmDto.builder()
                              .build();
        film.setName("Film");
        film.setDescription("a".repeat(201)); // 201 символ
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(film);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator()
                             .next()
                             .getMessage())
                .isEqualTo("Описание не должно превышать 200 символов");
    }

    @Test
    void shouldFailWhenReleaseDateBefore1895_12_28() {
        FilmDto film = FilmDto.builder()
                              .build();
        film.setName("Film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(1895, 12, 27)); // на день раньше
        film.setDuration(100);

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(film);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator()
                             .next()
                             .getMessage())
                .isEqualTo("Дата релиза не может быть раньше 28 декабря 1895 года");
    }

    @Test
    void shouldFailWhenReleaseDateIsNull() {
        FilmDto film = FilmDto.builder()
                              .build();
        film.setName("Film");
        film.setDescription("desc");
        film.setReleaseDate(null); // нарушение @NotNull
        film.setDuration(100);

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(film);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator()
                             .next()
                             .getMessage())
                .isEqualTo("не должно равняться null");
    }

    @Test
    void shouldFailWhenDurationIsZero() {
        FilmDto film = FilmDto.builder()
                              .build();
        film.setName("Film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(0);

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(film);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator()
                             .next()
                             .getMessage())
                .isEqualTo("Продолжительность должна быть положительным числом");
    }

    @Test
    void shouldFailWhenDurationIsNegative() {
        FilmDto film = FilmDto.builder()
                              .build();
        film.setName("Film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-5);

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(film);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator()
                             .next()
                             .getMessage())
                .isEqualTo("Продолжительность должна быть положительным числом");
    }

    @Test
    void shouldFailWhenMultipleViolations() {
        FilmDto film = FilmDto.builder()
                              .build();
        film.setName("");
        film.setDescription("a".repeat(300));
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        film.setDuration(-10);
        Set<ConstraintViolation<FilmDto>> violations = validator.validate(film);
        assertThat(violations).hasSize(4);
    }
}