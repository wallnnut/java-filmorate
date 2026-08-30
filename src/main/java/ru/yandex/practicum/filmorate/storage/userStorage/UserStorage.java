package ru.yandex.practicum.filmorate.storage.userStorage;

import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User addUser(User user);

    User updateUser(User user);

    User removeUser(Id id);

    List<User> getAllUsers();

    User getUserById(Id id);

    List<User> getUserByIds(List<Id> ids);

}
