package ru.yandex.practicum.filmorate.store;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.BaseEntity;

import java.util.*;

@Data
public class Store<T extends BaseEntity> {
    private Map<Long, T> store = new HashMap<>();
    private long lastId = 0;

    public Optional<T> getItemById(long id) {
        T element = store.get(id);
        if (element == null) {
            return Optional.empty();
        }
        return Optional.of(element);
    }

    public void add(T item) {
        store.put(item.getId(), item);
        lastId = item.getId();
    }

    public void edit(T item) {
        store.computeIfPresent(item.getId(), (key, value) -> item);
    }

    public List<T> getItems() {
        return new ArrayList<T>(store.values());
    }
}
