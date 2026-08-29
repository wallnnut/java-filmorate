package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.FriendShipService;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "filmorate.friendship.auto-accept=false",
        "filmorate.friendship.bidirectional=true"
})
@AutoConfigureTestDatabase
@Transactional
class FriendShipRequestModeTest {

    @Autowired
    private UserStorage userStorage;
    @Autowired
    private FriendShipService friendShipService;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void init() {
        user1 = userStorage.addUser(newUser("first-req@test.ru", "firstReq"));
        user2 = userStorage.addUser(newUser("second-req@test.ru", "secondReq"));
        user3 = userStorage.addUser(newUser("third-req@test.ru", "thirdReq"));
    }

    @Test
    void shouldKeepFriendsEmptyUntilRequestIsAccepted() {
        friendShipService.addFriend(user1.getId(), user2.getId());

        assertThat(friendShipService.getFriends(user1.getId())).isEmpty();
        assertThat(friendShipService.getFriends(user2.getId())).isEmpty();
    }

    @Test
    void shouldMakeFriendshipBidirectionalAfterAccept() {
        friendShipService.addFriend(user1.getId(), user2.getId());
        friendShipService.acceptFriend(user2.getId(), user1.getId());

        assertThat(friendShipService.getFriends(user1.getId()))
                .extracting(UserDto::getId)
                .containsExactly(user2.getId());
        assertThat(friendShipService.getFriends(user2.getId()))
                .extracting(UserDto::getId)
                .containsExactly(user1.getId());
    }

    @Test
    void shouldKeepBothListsEmptyAfterReject() {
        friendShipService.addFriend(user1.getId(), user2.getId());
        friendShipService.rejectFriend(user2.getId(), user1.getId());

        assertThat(friendShipService.getFriends(user1.getId())).isEmpty();
        assertThat(friendShipService.getFriends(user2.getId())).isEmpty();
    }

    @Test
    void shouldGetCommonFriendsAfterBothRequestsAccepted() {
        friendShipService.addFriend(user1.getId(), user3.getId());
        friendShipService.addFriend(user2.getId(), user3.getId());
        friendShipService.acceptFriend(user3.getId(), user1.getId());
        friendShipService.acceptFriend(user3.getId(), user2.getId());

        List<UserDto> common = friendShipService.getCommonFriends(user1.getId(), user2.getId());
        assertThat(common)
                .extracting(UserDto::getId)
                .containsExactly(user3.getId());
    }

    private static User newUser(String email, String login) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName(login);
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }
}
