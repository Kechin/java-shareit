package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.impl.UserServiceImpl;

import java.util.List;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;

    @PostMapping
    UserDto create(@RequestBody UserDto user) {
        return userService.create(user);
    }

    @PatchMapping("/{userId}")
    UserDto update(@PathVariable Long userId, @RequestBody UserDto userDto) {
        return userService.update(userDto, userId);
    }

    @GetMapping
    List<UserDto> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    UserDto get(@PathVariable Long userId) {
        return userService.get(userId);
    }

    @DeleteMapping("/{userId}")
    void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
