package ru.yandex.practicum.filmorate.dto.edit;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import ru.yandex.practicum.filmorate.dto.create.UserDto;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class UserEditDto extends UserDto {
    @NotNull
    private long id;

}