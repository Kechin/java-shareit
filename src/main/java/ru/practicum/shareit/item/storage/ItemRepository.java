package ru.practicum.shareit.item.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Repository
public class ItemRepository {
    private Long id = 0L;
    private final Map<Long, Item> itemStorage = new HashMap<>();
    private final Map<Long, List<Item>> userItemIndex = new LinkedHashMap<>();

    public Item add(Item item) {
        item.setId(++id);
        itemStorage.put(item.getId(), item);
        final List items = userItemIndex.computeIfAbsent(item.getOwner().getId(), k -> new ArrayList<>());
        items.add(item);
        return itemStorage.get(item.getId());
    }


    public Optional<Item> getById(Long id) {
        return Optional.ofNullable(itemStorage.get(id));
    }

    public Optional<List<Item>> getByUser(Long id) {
        return Optional.ofNullable(userItemIndex.get(id));
    }

    public List<Item> getByDescription(String text) {
        return itemStorage.values()
                .stream()
                .filter(i -> i.getDescription().toLowerCase().contains(text) || i.getName().toLowerCase().contains(text))
                .filter(Item::isAvailable)
                .collect(Collectors.toList());
    }
}
