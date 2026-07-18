package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.BaseEntity;
import ru.yandex.practicum.filmorate.model.Id;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class BaseStorage<T extends BaseEntity> {
    private final Map<Id, T> storage = new HashMap<>();
    private Id lastId = new Id(0);

    public T push(T item) {
        Id nextId = new Id(lastId.getId() + 1);
        storage.put(nextId, item);
        lastId = nextId;
        item.setId(nextId);
        return item;
    }

    public T getItemById(Id id) throws NotFoundException {
        if (storage.containsKey(id)) {
            return storage.get(id);
        }
        throw new NotFoundException(String.format("entity with id=%d does not exists", id.getId()));
    }

    public List<T> getItemByIds(List<Id> ids) throws NotFoundException {
        return ids.stream().map(storage::get).toList();
    }

    public T edit(T item) throws NotFoundException {
        Id itemId = item.getId();
        boolean isItemExists = storage.containsKey(itemId);
        if (isItemExists) {
            storage.put(itemId, item);
            return item;
        }
        throw new NotFoundException(String.format("entity with id=%d does not exists", itemId.getId()));
    }

    public T remove(Id id) throws NotFoundException {
        boolean isItemExists = storage.containsKey(id);
        if (isItemExists) {
            return storage.get(id);
        }
        throw new NotFoundException("entity does not exists");
    }

    public List<T> getList() {
        return storage.values().stream().toList();
    }

}
