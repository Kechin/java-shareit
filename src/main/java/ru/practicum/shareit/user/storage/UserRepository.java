package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepository {
    private Long id = 0L;
    private Set emails = new HashSet<String>();
    private Map<Long, User> storage = new HashMap<>();

    public User add(User user) {
        user.setId(++id);
        storage.put(user.getId(), user);
        return storage.get(user.getId());
    }


    public void delete(Long userId) {
        storage.remove(userId);
    }

    public User getUserById(Long id) {

        return Optional.ofNullable(storage.get(id)).orElseThrow(() ->
                new NotFoundException("Пользователя с указанным ID НЕТ!"));

    }

    public void addEmail(String email) {
        if (!emails.add(email)) {
            throw new RuntimeException("Пользователь с таким Email уже существует");
        }
    }

    public void delEmail(String email) {
        emails.remove(email);
    }

    public Collection<User> getUsers() {
        return storage.values();
    }
}
