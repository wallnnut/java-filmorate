package ru.yandex.practicum.filmorate.model;

public enum AgeRating {
    G("G", "Нет возрастных ограничений"),
    PG("PG", "Детям рекомендуется смотреть с родителями"),
    PG_13("PG-13", "Детям до 13 лет просмотр не желателен"),
    R("R", "Лицам до 17 лет только в присутствии взрослого"),
    NC_17("NC-17", "Лицам до 18 лет просмотр запрещён");

    private final String code;
    private final String russianDescription;

    AgeRating(String code, String russianDescription) {
        this.code = code;
        this.russianDescription = russianDescription;
    }

    public static AgeRating fromCode(String code) {
        for (AgeRating rating : values()) {
            if (rating.code.equals(code)) {
                return rating;
            }
        }
        throw new IllegalArgumentException("Неизвестный рейтинг: " + code);
    }

    public String getCode() {
        return code;
    }

    public String getRussianDescription() {
        return russianDescription;
    }

    @Override
    public String toString() {
        return code + " — " + russianDescription;
    }
}