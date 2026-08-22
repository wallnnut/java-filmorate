package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.yandex.practicum.filmorate.config.FriendshipProperties;

@SpringBootApplication
@EnableConfigurationProperties(FriendshipProperties.class)
public class FilmorateApplication {
    public static void main(String[] args) {
        System.setProperty("h2.bindAddress", "127.0.0.1");
        SpringApplication.run(FilmorateApplication.class, args);
    }

}
