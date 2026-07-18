package ru.yandex.practicum.filmorate.storage.friendsStorage;

import ru.yandex.practicum.filmorate.model.Id;

import java.util.List;

public interface FriendsStorage {
    public void addFriend(Id userId, Id friendId);

    public void removeFriend(Id userId, Id friendId);

    public List<Id> getFriendIds(Id userId);

    public List<Id> getCommonFriends(Id userId, Id friendId);
}
