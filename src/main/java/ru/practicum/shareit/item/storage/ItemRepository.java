package ru.practicum.shareit.item.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Repository
public class ItemRepository {
    private Long id = 0L;
    private final Map<Long, Item> storage = new HashMap<>();
    private final Map<Long, List<Item>> userItemIndex = new LinkedHashMap<>();

    public Item add(Item item) {
        item.setId(++id);
        storage.put(item.getId(), item);
        final List items = userItemIndex.computeIfAbsent(item.getOwner().getId(), k -> new ArrayList<>());
        items.add(item);
        return storage.get(item.getId());
    }



    public Item getById(Long id) {
        return storage.get(id);
    }

    public List<Item> getByUser(Long id) {
        return Optional.ofNullable(userItemIndex.get(id)).orElseThrow(() ->
                new NotFoundException("Пользователя с указанным ID НЕТ!"));
    }

    public List<Item> getByDescription(String text) {
        return storage.values()
                .stream()
                .filter(i -> i.getDescription().toLowerCase().contains(text) || i.getName().toLowerCase().contains(text))
                .filter(i -> i.isAvailable())
                .collect(Collectors.toList());
    }
}
