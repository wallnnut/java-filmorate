package ru.yandex.practicum.filmorate.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Id;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        UserStorage userStorage = new InMemoryUserStorageSet();
        userService = new UserService(userStorage);
    }

    private User createUser(String email, String login, String name, LocalDate birthday) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(birthday);
        return user;
    }

    @Test
    void addUser_shouldSaveAndReturnUserWithGeneratedId() {
        User user = createUser("user@mail.ru", "login1", "Name", LocalDate.of(1990, 1, 1));
        User saved = userService.addUser(user);

        assertNotNull(saved.getId());
        assertEquals("user@mail.ru", saved.getEmail());
        assertEquals(1L, saved.getId()
                              .getId());

        User fromStorage = userService.getUserById(new Id(1L));
        assertEquals(saved, fromStorage);
    }

    @Test
    void updateUser_shouldUpdateExistingUser() {
        User original = createUser("old@mail.ru", "oldLogin", "OldName", LocalDate.of(1990, 1, 1));
        User saved = userService.addUser(original);

        User updatedUser = new User();
        updatedUser.setId(saved.getId());
        updatedUser.setEmail("new@mail.ru");
        updatedUser.setLogin("newLogin");
        updatedUser.setName("NewName");
        updatedUser.setBirthday(LocalDate.of(2000, 2, 2));

        User updated = userService.updateUser(updatedUser);

        assertEquals(saved.getId(), updated.getId());
        assertEquals("new@mail.ru", updated.getEmail());
        assertEquals("newLogin", updated.getLogin());
        assertEquals("NewName", updated.getName());
        assertEquals(LocalDate.of(2000, 2, 2), updated.getBirthday());

        User fromStorage = userService.getUserById(new Id(saved.getId()
                                                               .getId()));
        assertEquals(updated, fromStorage);
    }

    @Test
    void updateUser_shouldThrowIfUserNotFound() {
        User user = new User();
        user.setId(new Id(999L));
        user.setEmail("missing@mail.ru");
        user.setLogin("missing");
        user.setName("Ghost");
        user.setBirthday(LocalDate.now());

        assertThrows(RuntimeException.class, () -> userService.updateUser(user));
    }

    @Test
    void removeUser_shouldRemoveAndReturnUser() {
        User user = createUser("delete@mail.ru", "deleteLogin", "Delete", LocalDate.of(1995, 5, 5));
        User saved = userService.addUser(user);

        Id id = new Id(saved.getId()
                            .getId());
        User removed = userService.removeUser(id);

        assertEquals(saved, removed);
        assertThrows(RuntimeException.class, () -> userService.getUserById(id));
    }

    @Test
    void removeUser_shouldThrowIfUserNotFound() {
        Id id = new Id(999L);
        assertThrows(RuntimeException.class, () -> userService.removeUser(id));
    }

    @Test
    void getAllUsers_shouldReturnAllAddedUsers() {
        User user1 = createUser("u1@mail.ru", "login1", "Name1", LocalDate.of(1990, 1, 1));
        User user2 = createUser("u2@mail.ru", "login2", "Name2", LocalDate.of(1991, 2, 2));

        userService.addUser(user1);
        userService.addUser(user2);

        List<User> all = userService.getAllUsers();
        assertEquals(2, all.size());
        assertTrue(all.stream()
                      .anyMatch(u -> "login1".equals(u.getLogin())));
        assertTrue(all.stream()
                      .anyMatch(u -> "login2".equals(u.getLogin())));
    }

    @Test
    void getUserById_shouldReturnCorrectUser() {
        User user = createUser("target@mail.ru", "targetLogin", "Target", LocalDate.of(2000, 1, 1));
        User saved = userService.addUser(user);

        User found = userService.getUserById(new Id(saved.getId()
                                                         .getId()));
        assertEquals(saved, found);
    }

    @Test
    void getUserById_shouldThrowIfNotFound() {
        Id id = new Id(999L);
        assertThrows(RuntimeException.class, () -> userService.getUserById(id));
    }

    @Test
    void getUserByIds_shouldReturnUsersForGivenIds() {
        User user1 = createUser("u1@mail.ru", "login1", "Name1", LocalDate.of(1990, 1, 1));
        User user2 = createUser("u2@mail.ru", "login2", "Name2", LocalDate.of(1991, 2, 2));
        User user3 = createUser("u3@mail.ru", "login3", "Name3", LocalDate.of(1992, 3, 3));

        User saved1 = userService.addUser(user1);
        User saved2 = userService.addUser(user2);
        User saved3 = userService.addUser(user3);

        List<Id> ids = List.of(
                new Id(saved1.getId()
                             .getId()),
                new Id(saved3.getId()
                             .getId())
        );

        List<User> found = userService.getUserByIds(ids);
        assertEquals(2, found.size());
        assertTrue(found.contains(saved1));
        assertTrue(found.contains(saved3));
        assertFalse(found.contains(saved2));
    }

    @Test
    void getUserByIds_shouldThrowIfAnyIdNotFound() {
        User user = createUser("u1@mail.ru", "login1", "Name1", LocalDate.of(1990, 1, 1));
        User saved = userService.addUser(user);

        List<Id> ids = List.of(
                new Id(saved.getId()
                            .getId()),
                new Id(999L)
        );

        assertThrows(RuntimeException.class, () -> userService.getUserByIds(ids));
    }

    @Test
    void addUser_shouldIncrementIdSequentially() {
        User u1 = createUser("u1@mail.ru", "l1", "N1", LocalDate.now());
        User u2 = createUser("u2@mail.ru", "l2", "N2", LocalDate.now());
        User saved1 = userService.addUser(u1);
        User saved2 = userService.addUser(u2);
        assertEquals(1L, saved1.getId()
                               .getId());
        assertEquals(2L, saved2.getId()
                               .getId());
    }

    private static class InMemoryUserStorageSet implements UserStorage {
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
                User user = getUserById(id);
                result.add(user);
            }
            return result;
        }
    }
}