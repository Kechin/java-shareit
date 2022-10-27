package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;


import javax.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto create(User user) {
        userRepository.save(user);
        return UserMapper.toUserDto(user);
    }
    @Override
    public UserDto update(UserDto userDto, Long id)  {
        log.info("Запрос на обновления данных пользователя " + id + " " + userDto);
        User oldUser = getUser(id);
        if (userDto.getEmail() != null) {
            oldUser.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            oldUser.setName(userDto.getName());
        }
        log.info("Запрос на обновления данных пользователя " + id + " " + userDto);
        userRepository.save(oldUser);
        return UserMapper.toUserDto(oldUser);
    }

    @Override
    public UserDto get(Long userId) {
        try {
            return UserMapper.toUserDto(getUser(userId));
        } catch (Throwable e) {
            throw new NotFoundException(e.getMessage());
        }
    }
   // @Transactional(readOnly = true)
     private User getUser(Long userId)  {

        return  userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("неверный ID"));
    }

    @Override
    public List<UserDto> getAll() {
        return UserMapper.userDtos(userRepository.findAll());
    }

    @Override
    public void delete(Long userId) {
        log.info("Запрос на удаления пользователя " + userId);
        userRepository.deleteById(userId);
    }



}
