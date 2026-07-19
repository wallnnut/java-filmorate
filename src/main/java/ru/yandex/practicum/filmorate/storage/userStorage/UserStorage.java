package ru.yandex.practicum.filmorate.storage.userStorage;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User addUser(User user);

    User updateUser(User user) throws NotFoundException;

    User removeUser(Id id) throws NotFoundException;

    List<User> getAllUsers();

    User getUserById(Id id) throws NotFoundException;

    List<User> getUserByIds(List<Id> ids) throws NotFoundException;

}
