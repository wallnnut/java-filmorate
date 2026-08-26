package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.services.FilmRatingService;
import ru.yandex.practicum.filmorate.services.FriendShipService;
import ru.yandex.practicum.filmorate.services.UserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final FriendShipService friendShipService;
    private final FilmRatingService filmRatingService;

    @PutMapping("/{userId}/friends/{friendId}")
    public void addFriend(@PathVariable Id userId, @PathVariable Id friendId) {
        log.info("User {} is adding friend {}", userId, friendId);
        friendShipService.addFriend(userId, friendId);
        log.info("Friendship between {} and {} established", userId, friendId);
    }

    @PutMapping("/{userId}/friends/{friendId}/accept")
    public void acceptFriend(@PathVariable Id userId, @PathVariable Id friendId) {
        log.info("User {} is accepting friend {}", userId, friendId);
        friendShipService.acceptFriend(userId, friendId);
        log.info("Friendship between {} and {} accepted", userId, friendId);
    }

    @PutMapping("/{userId}/friends/{friendId}/reject")
    public void rejectFriend(@PathVariable Id userId, @PathVariable Id friendId) {
        log.info("User {} is rejecting friend {}", userId, friendId);
        friendShipService.rejectFriend(userId, friendId);
        log.info("Friendship between {} and {} rejected", userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void removeFriend(@PathVariable Id userId, @PathVariable Id friendId) {
        log.info("User {} is removing friend {}", userId, friendId);
        friendShipService.removeFriend(userId, friendId);
        log.info("Friendship between {} and {} removed", userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public List<UserDto> getFriends(@PathVariable Id userId) {
        log.info("Request to get friends of user {}", userId);
        List<UserDto> friendsList = friendShipService.getFriends(userId);
        log.info("Found {} friends for user {}", friendsList.size(), userId);
        return friendsList;
    }

    @GetMapping("/{userId}/friends/common/{otherUserId}")
    public List<UserDto> getCommonFriends(@PathVariable Id userId, @PathVariable Id otherUserId) {
        log.info("Request to get common friends of users {} and {}", userId, otherUserId);
        List<UserDto> commonFriends = friendShipService.getCommonFriends(userId, otherUserId);
        log.info("Found {} common friends for users {} and {}", commonFriends.size(), userId, otherUserId);
        return commonFriends;
    }

    @PostMapping
    public UserDto createUser(@RequestBody @Valid UserDto user) {
        log.info("Creating user: {}", user);
        UserDto created = userService.addUser(user);
        log.info("User created with id {}: {}", created.getId(), created);
        return created;
    }

    @PutMapping
    public UserDto updateUser(@RequestBody @Valid UserDto user) {
        log.info("Updating user: {}", user);
        UserDto updated = userService.updateUser(user);
        log.info("User updated: {}", updated);
        return updated;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Request to get all users");
        List<UserDto> users = userService.getAllUsers();
        log.info("Returning {} users", users.size());
        return users;
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Id id) {
        log.info("Request to get user by id {}", id);
        UserDto user = userService.getUserById(id);
        log.info("Found user: {}", user);
        return user;
    }

    @GetMapping("/{id}/recommendations")
    public List<FilmDto> getRecommendations(@PathVariable Id id) {
        log.info("Received request for recommendations for user {}", id);
        List<FilmDto> recommendations = filmRatingService.getRecommendations(id);
        log.info("Returning {} recommendations for user {}", recommendations.size(), id);
        return recommendations;
    }

    @DeleteMapping("/{id}")
    public UserDto deleteUser(@PathVariable Id id) {
        log.info("Received request to delete user with id: {}", id);
        UserDto deletedUser = userService.removeUser(id);
        log.info("User deleted successfully: {}", deletedUser);
        return deletedUser;
    }
}