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
        log.info("User {} is adding friend {}", userId, friendId);
        friendShipService.addFriend(userId, friendId);
        log.info("Friendship between {} and {} established", userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void removeFriend(@PathVariable Id userId, @PathVariable Id friendId) {
        log.info("User {} is removing friend {}", userId, friendId);
        friendShipService.removeFriend(userId, friendId);
        log.info("Friendship between {} and {} removed", userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> getFriends(@PathVariable Id userId) {
        log.info("Request to get friends of user {}", userId);
        List<Id> friendsList = friendShipService.getFriends(userId);
        List<User> friends = userService.getUserByIds(friendsList);
        log.info("Found {} friends for user {}", friends.size(), userId);
        return friends;
    }

    @GetMapping("/{userId}/friends/common/{otherUserId}")
    public List<User> getCommonFriends(@PathVariable Id userId, @PathVariable Id otherUserId) {
        log.info("Request to get common friends of users {} and {}", userId, otherUserId);
        List<Id> friendsList = friendShipService.getCommonFriends(userId, otherUserId);
        List<User> commonFriends = userService.getUserByIds(friendsList);
        log.info("Found {} common friends for users {} and {}", commonFriends.size(), userId, otherUserId);
        return commonFriends;
    }

    @PostMapping
    public User createUser(@RequestBody @Valid UserDto user) {
        log.info("Creating user: {}", user);
        User created = userService.addUser(userMapper.toEntity(user));
        log.info("User created with id {}: {}", created.getId(), created);
        return created;
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid UserDto user) {
        log.info("Updating user: {}", user);
        User updated = userService.updateUser(userMapper.toEntity(user));
        log.info("User updated: {}", updated);
        return updated;
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Request to get all users");
        List<User> users = userService.getAllUsers();
        log.info("Returning {} users", users.size());
        return users;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Id id) {
        log.info("Request to get user by id {}", id);
        User user = userService.getUserById(id);
        log.info("Found user: {}", user);
        return user;
    }
}