package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.create.user.UserDto;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.store.Store;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@SuppressWarnings("unused")
@Slf4j
public class UserController {
    Store<User> userStore = new Store<>();

    @PostMapping
    public User add(@RequestBody @Valid UserDto newUser) {
        UUID id = UUID.randomUUID();

        User createdUser = User.builder()
                               .id(id)
                               .build();

        BeanUtils.copyProperties(newUser, createdUser);

        if (createdUser.getName() == null || createdUser.getName()
                                                        .isBlank()) {
            createdUser.setName(createdUser.getLogin());
        }

        userStore.add(createdUser);
        log.info("Пользователь создан: id={}, login={}", createdUser.getId(), createdUser.getLogin());
        return createdUser;
    }

    @GetMapping
    public List<User> getUsers() {
        return userStore.getItems();
    }

    @PutMapping("/{id}")
    public User edit(@PathVariable UUID id, @RequestBody @Valid UserDto user) {
        User foundUser = userStore.getItemById(id)
                                  .orElseThrow(() ->
                                          new ResourceNotFoundException("Пользователь с ID " + id + " не найден"));

        BeanUtils.copyProperties(user, foundUser);
        log.info("Пользователь обновлён: id={}, login={}", foundUser.getId(), foundUser.getLogin());
        return foundUser;
    }
}
