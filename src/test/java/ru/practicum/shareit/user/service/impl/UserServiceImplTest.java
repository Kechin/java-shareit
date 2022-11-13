package ru.practicum.shareit.user.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
class UserServiceImplTest {
    UserServiceImpl userService;
    UserRepository userRepository;
    private User user;
    private User user2;


    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
        user = (new User(1L, "user 1", "user1@email"));

    }

    @Test
    void create() {
        when(userRepository.save(any()))
                .thenReturn(user);
        Assertions.assertEquals(userService.create(UserMapper.toUserDto(user)),
                UserMapper.toUserDto(user));
    }

    @Test
    void update() {
        getUser();
        when(userRepository.save(any()))
                .thenReturn(user);
        Assertions.assertEquals(userService.update(UserMapper.toUserDto(user), 1L),
                UserMapper.toUserDto(user));
    }

    @Test
    void delete() {
        getUser();
        userService.delete(1L);

    }

    @Test
    void get() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        Assertions.assertEquals(userService.get(user.getId()), UserMapper.toUserDto(user));

    }

    @Test
    void getAll() {
        when(userRepository.findAll())
                .thenReturn(new ArrayList<>(Collections.singleton(user)));
        Assertions.assertEquals(userService.getAll(), new ArrayList<>(Collections.singleton(UserMapper.toUserDto(user))));
    }


    @AfterEach
    void after() {
        userRepository.deleteAll();
    }

    @Test
    void getUser() {
        when(userRepository.findById(anyLong())).thenAnswer(invocationOnMock -> {
            Long userId = invocationOnMock.getArgument(0, Long.class);
            if (userId > 3) {
                throw new NotFoundException(
                        String.format("User с указанным ID не найден"));
            } else {
                return Optional.of(user);
            }
        });
    }
}