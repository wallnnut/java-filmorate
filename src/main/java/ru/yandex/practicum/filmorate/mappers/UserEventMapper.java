package ru.yandex.practicum.filmorate.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.filmorate.dto.UserEventDto;
import ru.yandex.practicum.filmorate.model.UserEvent;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserEventMapper {
    @Mapping(source = "id", target = "eventId")
    UserEventDto toDto(UserEvent event);

    List<UserEventDto> toDto(List<UserEvent> events);
}
