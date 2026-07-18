package ru.yandex.practicum.filmorate.converter;

import org.springframework.stereotype.Component;
import org.springframework.core.convert.converter.Converter;
import ru.yandex.practicum.filmorate.model.Id;

@Component
public class StringToIdConverter implements Converter<String, Id> {
    @Override
    public Id convert(String source) {
        try {
            long value = Long.parseLong(source);
            return new Id(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid ID format: " + source);
        }
    }
}