package ru.yandex.practicum.filmorate.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendsStorage.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class FriendShipServiceTest {

    private FriendShipService friendShipService;
    private UserStorage userStorage;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        FriendsStorage friendsStorage = new InMemoryFriendsStorage();
        friendShipService = new FriendShipService(friendsStorage, userStorage);
    }

    private User createUser(String email, String login, String name, LocalDate birthday) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(birthday);
        return user;
    }

    private Id addTestUser(String email, String login) {
        User user = createUser(email, login, login, LocalDate.now());
        User saved = userStorage.addUser(user);
        return saved.getId();
    }

    @Test
    void addFriend_shouldAddFriendWhenValid() {
        Id userId = addTestUser("u1@mail.ru", "login1");
        Id friendId = addTestUser("u2@mail.ru", "login2");

        friendShipService.addFriend(userId, friendId);

        List<Id> friends = friendShipService.getFriends(userId);
        assertTrue(friends.contains(friendId));
    }

    @Test
    void addFriend_shouldDoNothingWhenUserIdIsNull() {
        Id friendId = addTestUser("u2@mail.ru", "login2");
        assertDoesNotThrow(() -> friendShipService.addFriend(null, friendId));
        List<Id> friends = friendShipService.getFriends(null);
        assertTrue(friends.isEmpty());
    }

    @Test
    void addFriend_shouldDoNothingWhenFriendIdIsNull() {
        Id userId = addTestUser("u1@mail.ru", "login1");
        assertDoesNotThrow(() -> friendShipService.addFriend(userId, null));
        List<Id> friends = friendShipService.getFriends(userId);
        assertTrue(friends.isEmpty());
    }

    @Test
    void addFriend_shouldDoNothingWhenBothIdsNull() {
        assertDoesNotThrow(() -> friendShipService.addFriend(null, null));
    }

    @Test
    void addFriend_shouldDoNothingWhenUserIdEqualsFriendId() {
        Id userId = addTestUser("u1@mail.ru", "login1");
        friendShipService.addFriend(userId, userId);
        List<Id> friends = friendShipService.getFriends(userId);
        assertTrue(friends.isEmpty());
    }

    @Test
    void addFriend_shouldThrowWhenUserNotFound() {
        Id userId = new Id(999L);
        Id friendId = addTestUser("u2@mail.ru", "login2");
        assertThrows(RuntimeException.class, () -> friendShipService.addFriend(userId, friendId));
    }

    @Test
    void addFriend_shouldThrowWhenFriendNotFound() {
        Id userId = addTestUser("u1@mail.ru", "login1");
        Id friendId = new Id(999L);
        assertThrows(RuntimeException.class, () -> friendShipService.addFriend(userId, friendId));
    }

    @Test
    void removeFriend_shouldRemoveExistingFriend() {
        Id userId = addTestUser("u1@mail.ru", "login1");
        Id friendId = addTestUser("u2@mail.ru", "login2");
        friendShipService.addFriend(userId, friendId);
        assertTrue(friendShipService.getFriends(userId)
                                    .contains(friendId));

        friendShipService.removeFriend(userId, friendId);
        List<Id> friends = friendShipService.getFriends(userId);
        assertFalse(friends.contains(friendId));
    }

    @Test
    void removeFriend_shouldDoNothingWhenFriendNotExists() {
        Id userId = addTestUser("u1@mail.ru", "login1");
        Id friendId = addTestUser("u2@mail.ru", "login2");
        friendShipService.removeFriend(userId, friendId);
        List<Id> friends = friendShipService.getFriends(userId);
        assertTrue(friends.isEmpty());
    }

    @Test
    void removeFriend_shouldDoNothingWhenUserIdNull() {
        Id friendId = addTestUser("u2@mail.ru", "login2");
        assertDoesNotThrow(() -> friendShipService.removeFriend(null, friendId));
    }

    @Test
    void removeFriend_shouldDoNothingWhenFriendIdNull() {
        Id userId = addTestUser("u1@mail.ru", "login1");
        assertDoesNotThrow(() -> friendShipService.removeFriend(userId, null));
    }

    @Test
    void removeFriend_shouldDoNothingWhenBothNull() {
        assertDoesNotThrow(() -> friendShipService.removeFriend(null, null));
    }

    @Test
    void removeFriend_shouldDoNothingWhenSameUser() {
        Id userId = addTestUser("u1@mail.ru", "login1");
        friendShipService.removeFriend(userId, userId);
        List<Id> friends = friendShipService.getFriends(userId);
        assertTrue(friends.isEmpty());
    }

    @Test
    void removeFriend_shouldThrowWhenUserNotFound() {
        Id userId = new Id(999L);
        Id friendId = addTestUser("u2@mail.ru", "login2");
        assertThrows(RuntimeException.class, () -> friendShipService.removeFriend(userId, friendId));
    }

    @Test
    void removeFriend_shouldThrowWhenFriendNotFound() {
        Id userId = addTestUser("u1@mail.ru", "login1");
        Id friendId = new Id(999L);
        assertThrows(RuntimeException.class, () -> friendShipService.removeFriend(userId, friendId));
    }

    @Test
    void getFriends_shouldReturnEmptyListWhenNoFriends() {
        Id userId = addTestUser("u1@mail.ru", "login1");
        List<Id> friends = friendShipService.getFriends(userId);
        assertTrue(friends.isEmpty());
    }

    @Test
    void getFriends_shouldReturnEmptyListWhenUserIdNull() {
        List<Id> friends = friendShipService.getFriends(null);
        assertTrue(friends.isEmpty());
    }

    @Test
    void getFriends_shouldThrowWhenUserNotFound() {
        Id userId = new Id(999L);
        assertThrows(RuntimeException.class, () -> friendShipService.getFriends(userId));
    }

    @Test
    void getFriends_shouldReturnAllAddedFriends() {
        Id user = addTestUser("u1@mail.ru", "login1");
        Id friend1 = addTestUser("u2@mail.ru", "login2");
        Id friend2 = addTestUser("u3@mail.ru", "login3");

        friendShipService.addFriend(user, friend1);
        friendShipService.addFriend(user, friend2);

        List<Id> friends = friendShipService.getFriends(user);
        assertEquals(2, friends.size());
        assertTrue(friends.contains(friend1));
        assertTrue(friends.contains(friend2));
    }

    @Test
    void getCommonFriends_shouldReturnCommonFriends() {
        Id userA = addTestUser("a@mail.ru", "loginA");
        Id userB = addTestUser("b@mail.ru", "loginB");
        Id commonFriend = addTestUser("common@mail.ru", "loginCommon");
        Id friendOnlyA = addTestUser("onlyA@mail.ru", "loginOnlyA");

        friendShipService.addFriend(userA, commonFriend);
        friendShipService.addFriend(userB, commonFriend);
        friendShipService.addFriend(userA, friendOnlyA); // только у A

        List<Id> common = friendShipService.getCommonFriends(userA, userB);
        assertEquals(1, common.size());
        assertTrue(common.contains(commonFriend));
        assertFalse(common.contains(friendOnlyA));
    }

    @Test
    void getCommonFriends_shouldReturnEmptyListWhenNoCommon() {
        Id userA = addTestUser("a@mail.ru", "loginA");
        Id userB = addTestUser("b@mail.ru", "loginB");
        Id friendA = addTestUser("onlyA@mail.ru", "loginOnlyA");
        friendShipService.addFriend(userA, friendA);

        List<Id> common = friendShipService.getCommonFriends(userA, userB);
        assertTrue(common.isEmpty());
    }

    @Test
    void getCommonFriends_shouldReturnFriendsOfUserWhenSameUser() {
        Id user = addTestUser("u@mail.ru", "login");
        Id friend1 = addTestUser("f1@mail.ru", "f1");
        Id friend2 = addTestUser("f2@mail.ru", "f2");
        friendShipService.addFriend(user, friend1);
        friendShipService.addFriend(user, friend2);

        List<Id> common = friendShipService.getCommonFriends(user, user);
        assertEquals(2, common.size());
        assertTrue(common.contains(friend1));
        assertTrue(common.contains(friend2));
    }

    @Test
    void getCommonFriends_shouldThrowWhenUserANotFound() {
        Id userA = new Id(999L);
        Id userB = addTestUser("b@mail.ru", "loginB");
        assertThrows(RuntimeException.class, () -> friendShipService.getCommonFriends(userA, userB));
    }

    @Test
    void getCommonFriends_shouldThrowWhenUserBNotFound() {
        Id userA = addTestUser("a@mail.ru", "loginA");
        Id userB = new Id(999L);
        assertThrows(RuntimeException.class, () -> friendShipService.getCommonFriends(userA, userB));
    }

    private static class InMemoryUserStorage implements UserStorage {
        private final Set<User> users = new HashSet<>();
        private long nextId = 1;

        @Override
        public User addUser(User user) {
            user.setId(new Id(nextId++));
            users.add(user);
            return user;
        }

        @Override
        public User updateUser(User user) {
            User existing = users.stream()
                                 .filter(u -> u.getId()
                                               .equals(user.getId()))
                                 .findFirst()
                                 .orElseThrow(() -> new RuntimeException("User not found"));
            users.remove(existing);
            users.add(user);
            return user;
        }

        @Override
        public User removeUser(Id id) {
            User removed = users.stream()
                                .filter(u -> u.getId()
                                              .equals(id))
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException("User not found"));
            users.remove(removed);
            return removed;
        }

        @Override
        public List<User> getAllUsers() {
            return new ArrayList<>(users);
        }

        @Override
        public User getUserById(Id id) {
            return users.stream()
                        .filter(u -> u.getId()
                                      .equals(id))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("User not found"));
        }

        @Override
        public List<User> getUserByIds(List<Id> ids) {
            List<User> result = new ArrayList<>();
            for (Id id : ids) {
                result.add(getUserById(id));
            }
            return result;
        }
    }

    private static class InMemoryFriendsStorage implements FriendsStorage {
        private final Map<Id, Set<Id>> friends = new HashMap<>();

        @Override
        public void addFriend(Id userId, Id friendId) {
            friends.computeIfAbsent(userId, k -> new HashSet<>())
                   .add(friendId);
        }

        @Override
        public void removeFriend(Id userId, Id friendId) {
            Set<Id> userFriends = friends.get(userId);
            if (userFriends != null) {
                userFriends.remove(friendId);
                if (userFriends.isEmpty()) {
                    friends.remove(userId);
                }
            }
        }

        @Override
        public List<Id> getFriendIds(Id userId) {
            Set<Id> userFriends = friends.get(userId);
            return userFriends == null ? Collections.emptyList() : new ArrayList<>(userFriends);
        }

        @Override
        public List<Id> getCommonFriends(Id userAId, Id userBId) {
            Set<Id> friendsA = friends.getOrDefault(userAId, Collections.emptySet());
            Set<Id> friendsB = friends.getOrDefault(userBId, Collections.emptySet());
            Set<Id> common = new HashSet<>(friendsA);
            common.retainAll(friendsB);
            return new ArrayList<>(common);
        }
    }
}