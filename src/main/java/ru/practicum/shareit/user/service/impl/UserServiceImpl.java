package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        userRepository.addEmail(userDto.getEmail());
        User user = userRepository.add(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(UserDto userDto, Long id) {
        log.info("Запрос на обновления данных пользователя " + id + " " + userDto);
        User oldUser = userRepository.getUserById(id);
        if (userDto.getEmail() != null) {
            userRepository.addEmail(userDto.getEmail());
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
        return UserMapper.toUserDto(userRepository.getUserById(userId));
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
        log.info("Запрос на удаления пользователя " + userId);

        userRepository.delEmail(userRepository.getUserById(userId).getEmail());
        userRepository.delete(userId);
    }
}
