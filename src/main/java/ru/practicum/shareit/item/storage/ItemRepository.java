package ru.practicum.shareit.item.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@Component
public class ItemRepository {
    private Long id = 0L;
    private Map<Long, Item> storage = new HashMap<>();

    public Item add(Item item) {
        item.setId(++id);
        storage.put(item.getId(), item);
        return storage.get(item.getId());
    }

    public Item update(Item item) {
        storage.replace(item.getId(), item);
        return storage.get(item.getId());
    }

    public Item getById(Long id) {
        return storage.get(id);
    }

    public List<Item> getByUser(Long id) {
        return storage.values()
                .stream()
                .filter(i -> i.getOwner().getId().equals(id))
                .collect(Collectors.toList());
    }

    public List<Item> getByDescription(String text) {
        return storage.values()
                .stream()
                .filter(i -> i.getDescription().toLowerCase().contains(text) || i.getName().toLowerCase().contains(text))
                .filter(i -> i.getAvailable())
                .collect(Collectors.toList());
    }
}
