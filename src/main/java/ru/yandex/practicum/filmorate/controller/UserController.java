package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.mappers.UserMapper;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.FriendShipService;
import ru.yandex.practicum.filmorate.services.UserService;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final FriendShipService friendShipService;

    @PutMapping("/{userId}/friends/{friendId}")
    public void addFriend(@PathVariable Id userId, @PathVariable Id friendId) {
        log.info("Пользователь id={} добавляет в друзья id={}", userId, friendId);
        log.debug("Попытка найти пользователя с id={}", userId);
        userService.getUserById(userId);
        log.debug("Попытка найти пользователя с id={}", friendId);
        userService.getUserById(friendId);
        friendShipService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void removeFriend(@PathVariable Id userId, @PathVariable Id friendId) {
        log.debug("Попытка найти пользователя с id={}", userId);
        userService.getUserById(userId);
        log.debug("Попытка найти пользователя с id={}", friendId);
        friendShipService.removeFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> getFriends(@PathVariable Id userId) {
        List<Id> friendsList = friendShipService.getFriends(userId);
        return userService.getUserByIds(friendsList);
    }

    @GetMapping("/{userId}/friends/common/{otherUserId}")
    public List<User> getCommonFriends(@PathVariable Id userId, @PathVariable Id otherUserId) {
        List<Id> friendsList = friendShipService.getCommonFriends(userId, otherUserId);
        return userService.getUserByIds(friendsList);
    }

    @PostMapping
    public User createUser(@RequestBody @Valid UserDto user) {
        log.info("Создание пользователя: {}", user);
        return userService.addUser(userMapper.toEntity(user));
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid UserDto user) {
        log.info("Обновление пользователя: {}", user);
        return userService.updateUser(userMapper.toEntity(user));
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Запрос списка всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Id id) {
        log.info("Запрос пользователя id={}", id);
        return userService.getUserById(id);
    }
}
