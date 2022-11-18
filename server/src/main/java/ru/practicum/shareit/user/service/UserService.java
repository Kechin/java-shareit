package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {


    UserDto create(UserDto user);

    UserDto update(UserDto userDto, Long id);

    UserDto get(Long userId);

    List<UserDto> getAll();

    void delete(Long userId);
}
