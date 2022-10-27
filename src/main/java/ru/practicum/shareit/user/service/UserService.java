package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {


    UserDto create(User user);

    UserDto update(UserDto userDto, Long id) throws Throwable;

    UserDto get(Long userId);

    List<UserDto> getAll();

    void delete(Long userId);
}
