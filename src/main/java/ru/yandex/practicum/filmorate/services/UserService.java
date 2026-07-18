package ru.yandex.practicum.filmorate.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    private final UserStorage userStore;

    public User addUser(User user) {
        log.info("Attempting to add user: {}", user);
        User added = userStore.addUser(user);
        log.info("User added successfully with id {}: {}", added.getId(), added);
        return added;
    }

    public User updateUser(User user) {
        log.info("Attempting to update user: {}", user);
        User updated = userStore.updateUser(user);
        log.info("User updated successfully: {}", updated);
        return updated;
    }

    public User removeUser(Id id) {
        log.info("Attempting to remove user with id {}", id);
        User removed = userStore.removeUser(id);
        log.info("User removed successfully: {}", removed);
        return removed;
    }

    public List<User> getAllUsers() {
        log.debug("Request to get all users");
        List<User> users = userStore.getAllUsers();
        log.info("Returning {} users", users.size());
        return users;
    }

    public User getUserById(Id id) {
        log.debug("Request to get user by id {}", id);
        User user = userStore.getUserById(id);
        log.info("Found user by id {}: {}", id, user);
        return user;
    }

    public List<User> getUserByIds(List<Id> ids) {
        if (ids == null || ids.isEmpty()) {
            log.warn("getUserByIds called with null or empty list of IDs");
            return Collections.emptyList();
        }
        log.info("Request to get users by IDs: {}", ids);
        List<User> users = userStore.getUserByIds(ids);
        log.info("Returning {} users for requested IDs", users.size());
        return users;
    }
}