package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepository {
    private Long id = 0L;
    private Set<String> emails = new HashSet<>();
    private Map<Long, User> storage = new HashMap<>();

    public User add(User user) {
        user.setId(++id);
        storage.put(user.getId(), user);
        return user;
    }


    public void delete(Long userId) {
        storage.remove(userId);
    }

    public Optional<User> getUserById(Long id) {
        System.out.println("запрос на пользовател");
        return Optional.ofNullable(storage.get(id));

    }

    public boolean addEmail(String email) {
        return emails.add(email);
    }

    public void delEmail(String email) {
        emails.remove(email);
    }

    public List<User> getUsers() {
        return List.copyOf(storage.values());
    }
}
