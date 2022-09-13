package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    @Autowired
    ItemService itemService;

    @PostMapping
    ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{id}")
    ItemDto update(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId
            , @Validated({Update.class}) @RequestBody ItemDto itemDto) {
        return itemService.update(id, itemDto, userId);
    }

    @GetMapping("/{id}")
    ItemDto getById(@PathVariable Long id) {
        return itemService.getById(id);
    }

    @GetMapping
    List<ItemDto> getByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getByUserId(userId);
    }

    @GetMapping("/search")
    List<ItemDto> getByText(@RequestParam String text) {

        return itemService.getByText(text);
    }
}
