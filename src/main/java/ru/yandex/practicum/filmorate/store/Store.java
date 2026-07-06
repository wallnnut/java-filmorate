package ru.yandex.practicum.filmorate.store;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.BaseEntity;

import java.util.*;

@Data
public class Store<T extends BaseEntity> {
    private Map<UUID, T> store = new HashMap<>();

    public Optional<T> getItemById(UUID id) {
        T element = store.get(id);
        if (element == null) {
            return Optional.empty();
        }
        return Optional.of(element);
    }

    public void add(T item) {
        store.put(item.getId(), item);
    }

    public void edit(T item) {
        store.computeIfPresent(item.getId(), (key, value) -> item);
    }

    public boolean delete(UUID id) {
        return store.remove(id) != null;
    }

    public List<T> getItems() {
        return new ArrayList<T>(store.values());
    }
}
