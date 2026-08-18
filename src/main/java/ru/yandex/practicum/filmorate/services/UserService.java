package ru.yandex.practicum.filmorate.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.mappers.UserMapper;
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
    private final UserMapper userMapper;

    public UserDto addUser(UserDto userDto) {
        log.info("Attempting to add user: {}", userDto);
        User added = userStore.addUser(toEntity(userDto));
        log.info("User added successfully with id {}: {}", added.getId(), added);
        return userMapper.toDto(added);
    }

    public UserDto updateUser(UserDto userDto) {
        log.info("Attempting to update user: {}", userDto);
        User updated = userStore.updateUser(toEntity(userDto));
        log.info("User updated successfully: {}", updated);
        return userMapper.toDto(updated);
    }

    public UserDto removeUser(Id id) {
        log.info("Attempting to remove user with id {}", id);
        User removed = userStore.removeUser(id);
        log.info("User removed successfully: {}", removed);
        return userMapper.toDto(removed);
    }

    public List<UserDto> getAllUsers() {
        log.debug("Request to get all users");
        List<User> users = userStore.getAllUsers();
        log.info("Returning {} users", users.size());
        return userMapper.toDto(users);
    }

    public UserDto getUserById(Id id) {
        log.debug("Request to get user by id {}", id);
        User user = userStore.getUserById(id);
        log.info("Found user by id {}: {}", id, user);
        return userMapper.toDto(user);
    }

    public List<UserDto> getUserByIds(List<Id> ids) {
        if (ids == null || ids.isEmpty()) {
            log.warn("getUserByIds called with null or empty list of IDs");
            return Collections.emptyList();
        }
        log.info("Request to get users by IDs: {}", ids);
        List<User> users = userStore.getUserByIds(ids);
        log.info("Returning {} users for requested IDs", users.size());
        return userMapper.toDto(users);
    }

    private User toEntity(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        user.setName(resolveName(userDto.getName(), userDto.getLogin()));
        return user;
    }

    private String resolveName(String name, String login) {
        return (name == null || name.isBlank()) ? login : name;
    }
}
