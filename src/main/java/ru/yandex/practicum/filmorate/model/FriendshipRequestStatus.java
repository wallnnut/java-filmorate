package ru.yandex.practicum.filmorate.model;

public enum FriendshipRequestStatus {
    ACCEPTED("Завяка принята"),
    PENDING("Завяка в ожидании"),
    REJECTED("Завяка отклонена");

    private final String description;

    FriendshipRequestStatus(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
