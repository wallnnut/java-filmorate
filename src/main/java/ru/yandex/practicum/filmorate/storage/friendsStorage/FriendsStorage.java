package ru.yandex.practicum.filmorate.storage.friendsStorage;

import ru.yandex.practicum.filmorate.model.Id;

import java.util.List;

public interface FriendsStorage {
    void addFriend(Id userId, Id friendId);

    void removeFriend(Id userId, Id friendId);

    List<Id> getFriendIds(Id userId);

    List<Id> getCommonFriends(Id userId, Id friendId);
}
