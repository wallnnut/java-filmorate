package ru.yandex.practicum.filmorate.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.storage.friendsStorage.FriendsStorage;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class FriendShipService {
    private final FriendsStorage friendsStorage;

    public void addFriend(Id userId, Id friendId) {
        if (userId == null || friendId == null) {
            log.warn("Attempt to add friend with null userId or friendId");
            return;
        }
        if (userId.equals(friendId)) {
            log.warn("User {} tried to add himself as friend", userId);
            return;
        }
        log.debug("Adding friend: user {} -> friend {}", userId, friendId);
        friendsStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Id userId, Id friendId) {
        if (userId == null || friendId == null) {
            log.warn("Attempt to remove friend with null userId or friendId");
            return;
        }
        if (userId.equals(friendId)) {
            log.warn("User {} tried to remove himself from friends", userId);
            return;
        }
        log.debug("Removing friend: user {} -> friend {}", userId, friendId);
        friendsStorage.removeFriend(userId, friendId);
    }

    public List<Id> getFriends(Id userId) {
        if (userId == null) {
            log.warn("getFriends called with null userId");
            return Collections.emptyList();
        }
        List<Id> friends = friendsStorage.getFriendIds(userId);
        log.debug("User {} has {} friends", userId, friends.size());
        return friends;
    }

    public List<Id> getCommonFriends(Id userAId, Id userBId) {
        if (userAId == null || userBId == null) {
            log.warn("getCommonFriends called with null id(s)");
            return Collections.emptyList();
        }
        if (userAId.equals(userBId)) {
            log.debug("User {} requested common friends with himself - returning his friends", userAId);
            return getFriends(userAId);
        }
        List<Id> common = friendsStorage.getCommonFriends(userAId, userBId);
        log.debug("Common friends between {} and {}: {}", userAId, userBId, common.size());
        return common;
    }
}