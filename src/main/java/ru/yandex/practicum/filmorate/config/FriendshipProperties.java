package ru.yandex.practicum.filmorate.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "filmorate.friendship")
public class FriendshipProperties {
    /**
     * true — заявка сразу становится ACCEPTED (контракт автотестов Практикума).
     * false — создаётся PENDING, дружба появляется только после accept.
     */
    private boolean autoAccept = true;

    /**
     * true — принятая заявка видна обоим пользователям.
     * false — в друзьях только исходящие ACCEPTED.
     */
    private boolean bidirectional = false;
}
