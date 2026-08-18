package ru.yandex.practicum.filmorate.storage.friendsStorage;

import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendsStorage {
    void addFriend(Id userId, Id friendId);

    void removeFriend(Id userId, Id friendId);

    List<User> getFriends(Id userId);

    List<User> getCommonFriends(Id userId, Id friendId);
}
