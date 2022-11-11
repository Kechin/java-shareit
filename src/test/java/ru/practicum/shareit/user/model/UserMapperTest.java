package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;

@SpringBootTest
class UserMapperTest {
    private User user;
    private UserDto userDto;

    @BeforeEach
    void before() {
        user = new User(1L, "user", "user@user.ru");
        userDto = UserMapper.toUserDto(user);
    }

    @Test
    void toUserDto() {
        Assertions.assertEquals(user.getId(), userDto.getId());

    }

    @Test
    void toUser() {
        Assertions.assertEquals(UserMapper.toUserDto(user).getId(), userDto.getId());
    }
}