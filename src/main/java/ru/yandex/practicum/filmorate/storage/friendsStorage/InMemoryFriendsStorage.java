package ru.yandex.practicum.filmorate.storage.friendsStorage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Id;

import java.util.*;

@Component
public class InMemoryFriendsStorage implements FriendsStorage {
    private final Map<Id, Set<Id>> friendsMap = new HashMap<>();

    @Override
    public void addFriend(Id userId, Id friendId) {
        if (userId.equals(friendId)) {
            return;
        }
        friendsMap.computeIfAbsent(userId, i -> new HashSet<>())
                  .add(friendId);
        friendsMap.computeIfAbsent(friendId, i -> new HashSet<>())
                  .add(userId);
    }

    @Override
    public void removeFriend(Id userId, Id friendId) {
        if (userId.equals(friendId)) {
            return;
        }
        Set<Id> friendsA = friendsMap.get(userId);
        if (friendsA != null) {
            friendsA.remove(friendId);
            if (friendsA.isEmpty()) {
                friendsMap.remove(userId);
            }
        }
        Set<Id> friendsB = friendsMap.get(friendId);
        if (friendsB != null) {
            friendsB.remove(userId);
            if (friendsB.isEmpty()) {
                friendsMap.remove(friendId);
            }
        }
    }

    @Override
    public List<Id> getFriendIds(Id userId) {
        return friendsMap.getOrDefault(userId, new HashSet<>())
                         .stream()
                         .toList();
    }

    @Override
    public List<Id> getCommonFriends(Id userIdA, Id userIdB) {
        Set<Id> friendsA = friendsMap.getOrDefault(userIdA, Collections.emptySet());
        Set<Id> friendsB = friendsMap.getOrDefault(userIdB, Collections.emptySet());

        Set<Id> common = new HashSet<>(friendsA);
        common.retainAll(friendsB);
        return common.stream()
                     .toList();
    }
}
