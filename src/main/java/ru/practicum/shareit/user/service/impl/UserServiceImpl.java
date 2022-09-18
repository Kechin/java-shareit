package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        addEmail(userDto.getEmail());
        User user = userRepository.add(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(UserDto userDto, Long id) {
        log.info("Запрос на обновления данных пользователя " + id + " " + userDto);
        User oldUser = getUser(id);
        if (userDto.getEmail() != null) {
            addEmail(userDto.getEmail());
            userRepository.delEmail(oldUser.getEmail());
            oldUser.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            oldUser.setName(userDto.getName());
        }
        return UserMapper.toUserDto(oldUser);
    }

    @Override
    public UserDto get(Long userId) {
        return UserMapper.toUserDto(getUser(userId));
    }

    private User getUser(Long userId) {
        return userRepository.getUserById(userId).orElseThrow(() ->
                new NotFoundException("Пользователя с указанным ID НЕТ!"));
    }

    @Override
    public List<UserDto> getAll() {

        return UserMapper.userDtos(userRepository.getUsers());
    }

    @Override
    public void delete(Long userId) {
        log.info("Запрос на удаления пользователя " + userId);
        userRepository.delEmail(getUser(userId).getEmail());
        userRepository.delete(userId);
    }

    private void addEmail(String email) {
        if (!userRepository.addEmail(email)) {
            throw new RuntimeException("Пользователь с таким Email уже существует");
        }
    }
}
