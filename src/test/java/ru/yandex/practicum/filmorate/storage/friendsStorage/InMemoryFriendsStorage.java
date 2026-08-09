package ru.yandex.practicum.filmorate.storage.friendsStorage;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class InMemoryFriendsStorage implements FriendsStorage {
    private final UserStorage userStorage;
    private final Map<Id, Set<Id>> friendsMap = new HashMap<>();

    @Override
    public void addFriend(Id userId, Id friendId) {
        if (userId.equals(friendId)) {
            return;
        }
        friendsMap.computeIfAbsent(userId, i -> new HashSet<>())
                .add(friendId);
    }

    @Override
    public void removeFriend(Id userId, Id friendId) {
        if (userId.equals(friendId)) {
            return;
        }
        Set<Id> friends = friendsMap.get(userId);
        if (friends != null) {
            friends.remove(friendId);
            if (friends.isEmpty()) {
                friendsMap.remove(userId);
            }
        }
    }

    @Override
    public List<User> getFriends(Id userId) {
        return friendsMap.getOrDefault(userId, Collections.emptySet())
                .stream()
                .sorted(Comparator.comparing(Id::getId))
                .map(userStorage::getUserById)
                .toList();
    }

    @Override
    public List<User> getCommonFriends(Id userIdA, Id userIdB) {
        Set<Id> friendsA = friendsMap.getOrDefault(userIdA, Collections.emptySet());
        Set<Id> friendsB = friendsMap.getOrDefault(userIdB, Collections.emptySet());

        Set<Id> common = new HashSet<>(friendsA);
        common.retainAll(friendsB);
        return common.stream()
                .sorted(Comparator.comparing(Id::getId))
                .map(userStorage::getUserById)
                .toList();
    }
}
