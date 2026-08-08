package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.mappers.UserMapper;
import ru.yandex.practicum.filmorate.mappers.UserMapperImpl;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.FriendShipService;
import ru.yandex.practicum.filmorate.services.UserService;
import ru.yandex.practicum.filmorate.storage.friendsStorage.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void setUp() {
        UserStorage userStorage = new InMemoryUserStorage();
        FriendsStorage friendsStorage = new InMemoryFriendsStorage();
        UserMapper userMapper = new UserMapperImpl();

        UserService userService = new UserService(userStorage, userMapper);
        FriendShipService friendShipService = new FriendShipService(friendsStorage, userStorage, userMapper);
        userController = new UserController(userService, friendShipService);
    }

    private UserDto createUser(String email, String login, String name, LocalDate birthday) {
        UserDto dto = new UserDto();
        dto.setEmail(email);
        dto.setLogin(login);
        dto.setName(name);
        dto.setBirthday(birthday);
        return userController.createUser(dto);
    }

    @Test
    void createUser_shouldSaveAndReturnUser() {
        UserDto user = createUser("test@mail.ru", "testLogin", "Test Name", LocalDate.of(1990, 1, 1));
        assertNotNull(user.getId());
        assertEquals("test@mail.ru", user.getEmail());
        assertEquals("testLogin", user.getLogin());
        assertEquals("Test Name", user.getName());
        assertEquals(LocalDate.of(1990, 1, 1), user.getBirthday());

        UserDto found = userController.getUserById(user.getId());
        assertEquals(user, found);
    }

    @Test
    void createUser_shouldHandleNullName() {
        UserDto user = createUser("test@mail.ru", "testLogin", null, LocalDate.now());
        assertEquals("testLogin", user.getName());
    }

    @Test
    void updateUser_shouldUpdateExistingUser() {
        UserDto created = createUser("old@mail.ru", "oldLogin", "Old", LocalDate.now());
        UserDto updateDto = new UserDto();
        updateDto.setId(created.getId());
        updateDto.setEmail("new@mail.ru");
        updateDto.setLogin("newLogin");
        updateDto.setName("New");
        updateDto.setBirthday(LocalDate.of(2000, 1, 1));

        UserDto updated = userController.updateUser(updateDto);
        assertEquals(created.getId(), updated.getId());
        assertEquals("new@mail.ru", updated.getEmail());
        assertEquals("newLogin", updated.getLogin());
        assertEquals("New", updated.getName());
        assertEquals(LocalDate.of(2000, 1, 1), updated.getBirthday());

        UserDto fromStorage = userController.getUserById(created.getId());
        assertEquals(updated, fromStorage);
    }

    @Test
    void updateUser_shouldThrowIfUserNotFound() {
        UserDto dto = new UserDto();
        dto.setId(new Id(999L));
        dto.setEmail("missing@mail.ru");
        assertThrows(RuntimeException.class, () -> userController.updateUser(dto));
    }

    @Test
    void getAllUsers_shouldReturnAll() {
        UserDto u1 = createUser("u1@mail.ru", "l1", "N1", LocalDate.now());
        UserDto u2 = createUser("u2@mail.ru", "l2", "N2", LocalDate.now());

        List<UserDto> all = userController.getAllUsers();
        assertEquals(2, all.size());
        assertTrue(all.contains(u1));
        assertTrue(all.contains(u2));
    }

    @Test
    void getAllUsers_shouldReturnEmptyListWhenNoUsers() {
        List<UserDto> all = userController.getAllUsers();
        assertTrue(all.isEmpty());
    }

    @Test
    void getUserById_shouldReturnUser() {
        UserDto created = createUser("test@mail.ru", "login", "Name", LocalDate.now());
        UserDto found = userController.getUserById(created.getId());
        assertEquals(created, found);
    }

    @Test
    void getUserById_shouldThrowIfNotFound() {
        assertThrows(RuntimeException.class, () -> userController.getUserById(new Id(999L)));
    }

    @Test
    void addFriend_shouldAddFriend() {
        UserDto user = createUser("u1@mail.ru", "l1", "N1", LocalDate.now());
        UserDto friend = createUser("u2@mail.ru", "l2", "N2", LocalDate.now());

        userController.addFriend(user.getId(), friend.getId());

        List<UserDto> friends = userController.getFriends(user.getId());
        assertEquals(1, friends.size());
        assertEquals(friend, friends.get(0));
    }

    @Test
    void addFriend_shouldThrowIfUserNotFound() {
        UserDto friend = createUser("u2@mail.ru", "l2", "N2", LocalDate.now());
        assertThrows(RuntimeException.class, () -> userController.addFriend(new Id(999L), friend.getId()));
    }

    @Test
    void addFriend_shouldThrowIfFriendNotFound() {
        UserDto user = createUser("u1@mail.ru", "l1", "N1", LocalDate.now());
        assertThrows(RuntimeException.class, () -> userController.addFriend(user.getId(), new Id(999L)));
    }

    @Test
    void removeFriend_shouldRemoveExistingFriend() {
        UserDto user = createUser("u1@mail.ru", "l1", "N1", LocalDate.now());
        UserDto friend = createUser("u2@mail.ru", "l2", "N2", LocalDate.now());
        userController.addFriend(user.getId(), friend.getId());
        assertFalse(userController.getFriends(user.getId())
                                  .isEmpty());

        userController.removeFriend(user.getId(), friend.getId());
        List<UserDto> friends = userController.getFriends(user.getId());
        assertTrue(friends.isEmpty());
    }

    @Test
    void removeFriend_shouldDoNothingIfFriendNotExists() {
        UserDto user = createUser("u1@mail.ru", "l1", "N1", LocalDate.now());
        UserDto friend = createUser("u2@mail.ru", "l2", "N2", LocalDate.now());
        userController.removeFriend(user.getId(), friend.getId());
        assertTrue(userController.getFriends(user.getId())
                                 .isEmpty());
    }

    @Test
    void removeFriend_shouldThrowIfUserNotFound() {
        UserDto friend = createUser("u2@mail.ru", "l2", "N2", LocalDate.now());
        assertThrows(RuntimeException.class, () -> userController.removeFriend(new Id(999L), friend.getId()));
    }

    @Test
    void removeFriend_shouldThrowIfFriendNotFound() {
        UserDto user = createUser("u1@mail.ru", "l1", "N1", LocalDate.now());
        assertThrows(RuntimeException.class, () -> userController.removeFriend(user.getId(), new Id(999L)));
    }

    @Test
    void getFriends_shouldReturnAllFriends() {
        UserDto user = createUser("u1@mail.ru", "l1", "N1", LocalDate.now());
        UserDto f1 = createUser("f1@mail.ru", "fl1", "FN1", LocalDate.now());
        UserDto f2 = createUser("f2@mail.ru", "fl2", "FN2", LocalDate.now());

        userController.addFriend(user.getId(), f1.getId());
        userController.addFriend(user.getId(), f2.getId());

        List<UserDto> friends = userController.getFriends(user.getId());
        assertEquals(2, friends.size());
        assertTrue(friends.contains(f1));
        assertTrue(friends.contains(f2));
    }

    @Test
    void getFriends_shouldReturnEmptyListWhenNoFriends() {
        UserDto user = createUser("u1@mail.ru", "l1", "N1", LocalDate.now());
        List<UserDto> friends = userController.getFriends(user.getId());
        assertTrue(friends.isEmpty());
    }

    @Test
    void getFriends_shouldThrowIfUserNotFound() {
        assertThrows(RuntimeException.class, () -> userController.getFriends(new Id(999L)));
    }

    @Test
    void getCommonFriends_shouldReturnCommonFriends() {
        UserDto userA = createUser("a@mail.ru", "la", "NA", LocalDate.now());
        UserDto userB = createUser("b@mail.ru", "lb", "NB", LocalDate.now());
        UserDto common = createUser("common@mail.ru", "lc", "NC", LocalDate.now());
        UserDto onlyA = createUser("onlyA@mail.ru", "loA", "NOA", LocalDate.now());

        userController.addFriend(userA.getId(), common.getId());
        userController.addFriend(userB.getId(), common.getId());
        userController.addFriend(userA.getId(), onlyA.getId());

        List<UserDto> commonFriends = userController.getCommonFriends(userA.getId(), userB.getId());
        assertEquals(1, commonFriends.size());
        assertEquals(common, commonFriends.get(0));
    }

    @Test
    void getCommonFriends_shouldReturnEmptyListWhenNoCommon() {
        UserDto userA = createUser("a@mail.ru", "la", "NA", LocalDate.now());
        UserDto userB = createUser("b@mail.ru", "lb", "NB", LocalDate.now());
        UserDto onlyA = createUser("onlyA@mail.ru", "loA", "NOA", LocalDate.now());
        userController.addFriend(userA.getId(), onlyA.getId());

        List<UserDto> commonFriends = userController.getCommonFriends(userA.getId(), userB.getId());
        assertTrue(commonFriends.isEmpty());
    }

    @Test
    void getCommonFriends_shouldReturnFriendsOfUserWhenSameId() {
        UserDto user = createUser("u@mail.ru", "l", "N", LocalDate.now());
        UserDto f1 = createUser("f1@mail.ru", "lf1", "NF1", LocalDate.now());
        UserDto f2 = createUser("f2@mail.ru", "lf2", "NF2", LocalDate.now());
        userController.addFriend(user.getId(), f1.getId());
        userController.addFriend(user.getId(), f2.getId());

        List<UserDto> common = userController.getCommonFriends(user.getId(), user.getId());
        assertEquals(2, common.size());
        assertTrue(common.contains(f1));
        assertTrue(common.contains(f2));
    }

    @Test
    void getCommonFriends_shouldThrowIfUserANotFound() {
        UserDto userB = createUser("b@mail.ru", "lb", "NB", LocalDate.now());
        assertThrows(RuntimeException.class, () -> userController.getCommonFriends(new Id(999L), userB.getId()));
    }

    @Test
    void getCommonFriends_shouldThrowIfUserBNotFound() {
        UserDto userA = createUser("a@mail.ru", "la", "NA", LocalDate.now());
        assertThrows(RuntimeException.class, () -> userController.getCommonFriends(userA.getId(), new Id(999L)));
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