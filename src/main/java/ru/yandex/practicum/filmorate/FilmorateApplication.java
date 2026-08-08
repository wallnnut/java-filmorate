package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FilmorateApplication {
    public static void main(String[] args) {
        System.setProperty("h2.bindAddress", "127.0.0.1");
        SpringApplication.run(FilmorateApplication.class, args);
    }

}
