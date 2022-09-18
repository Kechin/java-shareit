package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    UserDto create(@Validated({Create.class}) @RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @PatchMapping("/{userId}")
    UserDto update(@PathVariable Long userId, @Validated({Update.class}) @RequestBody UserDto userDto) {
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
