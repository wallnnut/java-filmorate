package ru.yandex.practicum.filmorate.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.storage.friendsStorage.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class FriendShipService {
    private final FriendsStorage friendsStorage;
    private final UserStorage userStorage;

    public void addFriend(Id userId, Id friendId) {
        log.info("Adding friend: user {} -> friend {}", userId, friendId);
        if (userId == null || friendId == null) {
            log.warn("Attempt to add friend with null userId or friendId");
            return;
        }
        if (userId.equals(friendId)) {
            log.warn("User {} tried to add himself as friend", userId);
            return;
        }
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        log.debug("Validation passed, storing friendship");
        friendsStorage.addFriend(userId, friendId);
        log.info("Friendship between {} and {} successfully added", userId, friendId);
    }

    public void removeFriend(Id userId, Id friendId) {
        log.info("Removing friend: user {} -> friend {}", userId, friendId);
        if (userId == null || friendId == null) {
            log.warn("Attempt to remove friend with null userId or friendId");
            return;
        }
        if (userId.equals(friendId)) {
            log.warn("User {} tried to remove himself from friends", userId);
            return;
        }
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        log.debug("Validation passed, removing friendship");
        friendsStorage.removeFriend(userId, friendId);
        log.info("Friendship between {} and {} successfully removed", userId, friendId);
    }

    public List<Id> getFriends(Id userId) {
        log.info("Request to get friends of user {}", userId);
        if (userId == null) {
            log.warn("getFriends called with null userId");
            return Collections.emptyList();
        }
        userStorage.getUserById(userId);
        List<Id> friends = friendsStorage.getFriendIds(userId);
        log.info("User {} has {} friends", userId, friends.size());
        log.debug("Friends list: {}", friends);
        return friends;
    }

    public List<Id> getCommonFriends(Id userAId, Id userBId) {
        log.info("Request to get common friends of users {} and {}", userAId, userBId);
        if (userAId == null || userBId == null) {
            log.warn("getCommonFriends called with null id(s)");
            return Collections.emptyList();
        }
        userStorage.getUserById(userAId);
        userStorage.getUserById(userBId);
        if (userAId.equals(userBId)) {
            log.debug("User {} requested common friends with himself - returning his friends", userAId);
            return getFriends(userAId);
        }
        List<Id> common = friendsStorage.getCommonFriends(userAId, userBId);
        log.info("Found {} common friends between {} and {}", common.size(), userAId, userBId);
        log.debug("Common friends list: {}", common);
        return common;
    }
}