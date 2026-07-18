package ru.yandex.practicum.filmorate.validators;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReleaseDateValidatorTest {

    private final ReleaseDateValidator validator = new ReleaseDateValidator();

    @Test
    void isValid_shouldReturnTrue_whenValueIsNull() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void isValid_shouldReturnTrue_whenValueEqualsMinDate() {
        LocalDate minDate = LocalDate.of(1895, 12, 28);
        assertTrue(validator.isValid(minDate, null));
    }

    @Test
    void isValid_shouldReturnTrue_whenValueAfterMinDate() {
        LocalDate date = LocalDate.of(2000, 1, 1);
        assertTrue(validator.isValid(date, null));
    }

    @Test
    void isValid_shouldReturnFalse_whenValueBeforeMinDate() {
        LocalDate date = LocalDate.of(1895, 12, 27);
        assertFalse(validator.isValid(date, null));
    }

    @Test
    void isValid_shouldReturnTrue_whenValueIsOneDayAfterMin() {
        LocalDate date = LocalDate.of(1895, 12, 29);
        assertTrue(validator.isValid(date, null));
    }
}