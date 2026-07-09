package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.create.UserDto;
import ru.yandex.practicum.filmorate.dto.edit.UserEditDto;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.store.Store;

import java.util.List;

@RestController
@RequestMapping("/users")
@SuppressWarnings("unused")
@Slf4j
public class UserController {
    Store<User> userStore = new Store<>();

    @PostMapping
    public User add(@RequestBody @Valid UserDto newUser) {
        long id = userStore.getLastId() + 1;

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

    @PutMapping
    public User edit(@RequestBody @Valid UserEditDto user) {
        User foundUser = userStore.getItemById(user.getId())
                                  .orElseThrow(() ->
                                          new ResourceNotFoundException("Пользователь с ID " + user.getId() + " не найден"));

        userStore.edit(foundUser);
        log.info("Пользователь обновлён: id={}, login={}", foundUser.getId(), foundUser.getLogin());
        return foundUser;
    }
}
