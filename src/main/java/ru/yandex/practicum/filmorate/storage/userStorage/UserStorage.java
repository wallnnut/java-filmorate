package ru.yandex.practicum.filmorate.storage.userStorage;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    public User addUser(User user);
    public User updateUser(User user) throws NotFoundException;
    public User removeUser(Id id) throws NotFoundException;
    public List<User> getAllUsers();
    public User getUserById(Id id) throws NotFoundException;
    public List<User> getUserByIds(List<Id> ids) throws NotFoundException;

}
