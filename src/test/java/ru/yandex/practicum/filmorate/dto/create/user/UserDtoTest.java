package ru.yandex.practicum.filmorate.dto.create.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.create.UserDto;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldValidateCorrectUser() {
        UserDto user = UserDto.builder()
                              .build();
        ;
        user.setEmail("user@example.com");
        user.setLogin("ivanov");
        user.setName("Ivan Ivanov");
        user.setBirthday(LocalDate.of(1990, 5, 15));

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldPassWhenNameIsNull() {
        UserDto user = UserDto.builder()
                              .build();
        user.setEmail("user@example.com");
        user.setLogin("ivanov");
        user.setName(null);
        user.setBirthday(LocalDate.of(1990, 5, 15));

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldPassWhenNameIsEmpty() {
        UserDto user = UserDto.builder()
                              .build();
        ;
        user.setEmail("user@example.com");
        user.setLogin("ivanov");
        user.setName("");
        user.setBirthday(LocalDate.of(1990, 5, 15));

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldPassWhenBirthdayIsToday() {
        UserDto user = UserDto.builder()
                              .build();
        ;
        user.setEmail("user@example.com");
        user.setLogin("ivanov");
        user.setName("Ivan");
        user.setBirthday(LocalDate.now());

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldPassWhenLoginContainsSpaces() {
        UserDto user = UserDto.builder()
                              .build();
        ;
        user.setEmail("user@example.com");
        user.setLogin("ivan ov");
        user.setName("Ivan");
        user.setBirthday(LocalDate.of(1990, 5, 15));

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldPassWhenLoginStartsWithSpace() {
        UserDto user = UserDto.builder()
                              .build();
        ;
        user.setEmail("user@example.com");
        user.setLogin(" ivanov");
        user.setName("Ivan");
        user.setBirthday(LocalDate.of(1990, 5, 15));

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldPassWhenLoginEndsWithSpace() {
        UserDto user = UserDto.builder()
                              .build();
        ;
        user.setEmail("user@example.com");
        user.setLogin("ivanov ");
        user.setName("Ivan");
        user.setBirthday(LocalDate.of(1990, 5, 15));

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailWhenEmailIsBlank() {
        UserDto user = UserDto.builder()
                              .build();
        ;
        user.setEmail("");
        user.setLogin("ivanov");
        user.setName("Ivan");
        user.setBirthday(LocalDate.of(1990, 5, 15));

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator()
                             .next()
                             .getMessage())
                .isEqualTo("Email не может быть пустым");
    }

    @Test
    void shouldFailWhenEmailIsNull() {
        UserDto user = UserDto.builder()
                              .build();
        ;
        user.setEmail(null);
        user.setLogin("ivanov");
        user.setName("Ivan");
        user.setBirthday(LocalDate.of(1990, 5, 15));

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator()
                             .next()
                             .getMessage())
                .isEqualTo("Email не может быть пустым");
    }

    @Test
    void shouldFailWhenEmailDoesNotContainAt() {
        UserDto user = UserDto.builder()
                              .build();
        ;
        user.setEmail("userexample.com");
        user.setLogin("ivanov");
        user.setName("Ivan");
        user.setBirthday(LocalDate.of(1990, 5, 15));

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator()
                             .next()
                             .getMessage())
                .isEqualTo("Email должен содержать символ @ и быть корректным");
    }

    @Test
    void shouldFailWhenEmailIsInvalid() {
        UserDto user = UserDto.builder()
                              .build();
        ;
        user.setEmail("user@");
        user.setLogin("ivanov");
        user.setName("Ivan");
        user.setBirthday(LocalDate.of(1990, 5, 15));

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator()
                             .next()
                             .getMessage())
                .isEqualTo("Email должен содержать символ @ и быть корректным");
    }

    @Test
    void shouldFailWhenLoginIsBlank() {
        UserDto user = UserDto.builder()
                              .build();
        ;
        user.setEmail("user@example.com");
        user.setLogin("");
        user.setName("Ivan");
        user.setBirthday(LocalDate.of(1990, 5, 15));

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator()
                             .next()
                             .getMessage())
                .isEqualTo("Логин не может быть пустым");
    }

    @Test
    void shouldFailWhenLoginIsNull() {
        UserDto user = UserDto.builder()
                              .build();
        ;
        user.setEmail("user@example.com");
        user.setLogin(null);
        user.setName("Ivan");
        user.setBirthday(LocalDate.of(1990, 5, 15));

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator()
                             .next()
                             .getMessage())
                .isEqualTo("Логин не может быть пустым");
    }

    @Test
    void shouldFailWhenLoginIsOnlySpaces() {
        UserDto user = UserDto.builder()
                              .build();
        ;
        user.setEmail("user@example.com");
        user.setLogin("   ");
        user.setName("Ivan");
        user.setBirthday(LocalDate.of(1990, 5, 15));

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator()
                             .next()
                             .getMessage())
                .isEqualTo("Логин не может быть пустым");
    }

    @Test
    void shouldFailWhenBirthdayIsNull() {
        UserDto user = UserDto.builder()
                              .build();
        ;
        user.setEmail("user@example.com");
        user.setLogin("ivanov");
        user.setName("Ivan");
        user.setBirthday(null);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator()
                             .next()
                             .getMessage())
                .isEqualTo("Дата рождения обязательна");
    }

    @Test
    void shouldFailWhenBirthdayIsInFuture() {
        UserDto user = UserDto.builder()
                              .build();
        ;
        user.setEmail("user@example.com");
        user.setLogin("ivanov");
        user.setName("Ivan");
        user.setBirthday(LocalDate.now()
                                  .plusDays(1));

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator()
                             .next()
                             .getMessage())
                .isEqualTo("Дата рождения не может быть в будущем");
    }

    @Test
    void shouldFailWhenMultipleViolations() {
        UserDto user = UserDto.builder()
                              .build();
        ;
        user.setEmail("invalid");
        user.setLogin("");
        user.setName("Ivan");
        user.setBirthday(LocalDate.now()
                                  .plusDays(1));

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertThat(violations).hasSize(3);
    }
}