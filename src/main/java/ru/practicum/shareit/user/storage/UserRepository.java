package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;

@Component
public class UserRepository {
    private Long id = 0L;
    private HashMap<Long, User> storage = new HashMap<>();

    public User add(User user) {
        user.setId(++id);
        storage.put(user.getId(), user);
        return storage.get(user.getId());
    }

    public User update(User user) {
        storage.put(user.getId(), user);
        return storage.get(user.getId());
    }

    public void delete(Long userId) {
        storage.remove(userId);
    }

    public User getUser(Long id) {
        if (!storage.containsKey(id)) {
            throw new NotFoundException("Пользователя с указанным ID НЕТ!");
        }

        return storage.get(id);
    }

    public Collection<User> getUsers() {
        return storage.values();
    }
}
