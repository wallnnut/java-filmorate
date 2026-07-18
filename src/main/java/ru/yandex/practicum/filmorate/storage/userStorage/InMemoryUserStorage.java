package ru.yandex.practicum.filmorate.storage.userStorage;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;

@Component
@AllArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    BaseStorage<User> userStorage;

    @Override
    public User addUser(User user) {
        return userStorage.push(user);
    }

    @Override
    public User updateUser(User user) {
        return userStorage.edit(user);
    }

    @Override
    public User removeUser(Id id) {
        return userStorage.remove(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userStorage.getList();
    }

    @Override
    public User getUserById(Id id) throws NotFoundException {
        return userStorage.getItemById(id);
    }

    @Override
    public List<User> getUserByIds(List<Id> ids) throws NotFoundException {
        return userStorage.getItemByIds(ids);
    }
}
