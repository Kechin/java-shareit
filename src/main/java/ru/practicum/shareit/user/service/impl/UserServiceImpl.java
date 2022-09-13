package ru.practicum.shareit.user.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private Set emails = new HashSet<>();
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        emailCheck(userDto.getEmail());
        User user = userRepository.add(UserMapper.toUser(userDto));
        emails.add(user.getEmail());
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(UserDto userDto, Long id) {
        log.info("Запрос на обновления данных пользователя " + id + " " + userDto);
        User oldUser = userRepository.getUser(id);
        if (userDto.getEmail() != null) {
            emailCheck(userDto.getEmail());
            emails.add(userDto.getEmail());
            emails.remove(oldUser.getEmail());
        } else {
            userDto.setEmail(oldUser.getEmail());
        }

        if (userDto.getName() == null) {
            userDto.setName(oldUser.getName());
        }
        userDto.setId(id);
        return UserMapper.toUserDto(userRepository.update(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto get(Long userId) {
        return UserMapper.toUserDto(userRepository.getUser(userId));
    }

    @Override
    public List<UserDto> getAll() {
        ArrayList<UserDto> userDtos = new ArrayList<>();
        for (User user : userRepository.getUsers()) {
            userDtos.add(UserMapper.toUserDto(user));
        }
        return userDtos;
    }

    @Override
    public void delete(Long userId) {
        emails.remove(userRepository.getUser(userId).getEmail());
        userRepository.delete(userId);
    }

    private void emailCheck(String email) {
        if (emails.contains(email)) {
            throw new RuntimeException("Пользователь с таким Email уже существует");
        }
        ;
    }
}
