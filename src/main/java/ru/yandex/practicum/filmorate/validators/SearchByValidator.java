package ru.yandex.practicum.filmorate.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.Set;

public class SearchByValidator implements ConstraintValidator<ValidSearchBy, List<String>> {
    private Set<String> allowed;

    @Override
    public void initialize(ValidSearchBy annotation) {
        allowed = Set.of(annotation.value());
    }

    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return allowed.containsAll(value);
    }
}