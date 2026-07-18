package ru.yandex.practicum.filmorate.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    private final UserStorage userStore;

    public User addUser(User user) {
        return userStore.addUser(user);
    }

    public User updateUser(User user) {
        return userStore.updateUser(user);
    }

    public User removeUser(Id id) {
        return userStore.removeUser(id);
    }

    public List<User> getAllUsers() {
        return userStore.getAllUsers();
    }

    public User getUserById(Id id) {
        return userStore.getUserById(id);
    }

    public List<User> getUserByIds(List<Id> ids) {
        return userStore.getUserByIds(ids);
    }


}
