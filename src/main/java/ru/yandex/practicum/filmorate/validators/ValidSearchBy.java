package ru.yandex.practicum.filmorate.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SearchByValidator.class)
public @interface ValidSearchBy {
    String[] value();

    String message() default "Invalid params for by";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}