package ru.practicum.shareit.user.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

@Slf4j
@Service

@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;


    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.userRepository = repository;

    }

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        log.info("Запрос на добавления нового пользователя" + userDto);
        User user = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto update(UserDto userDto, Long id) {
        log.info("Запрос на обновления данных пользователя " + id + " " + userDto);
        User oldUser = getUser(id);
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            oldUser.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            oldUser.setName(userDto.getName());
        }
        log.info("Запрос на обновления данных пользователя " + id + " " + userDto);

        return UserMapper.toUserDto(oldUser);
    }

    @Override
    public UserDto get(Long userId) {
        return UserMapper.toUserDto(getUser(userId));
    }


    @Override
    public List<UserDto> getAll() {
        return UserMapper.userDtos(userRepository.findAll());
    }

    @Transactional
    @Override
    public void delete(Long userId) {
        getUser(userId);
        log.info("Запрос на удаления пользователя " + userId);
        userRepository.deleteById(userId);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("неверный User ID"));
    }


}
