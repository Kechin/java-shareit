package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;
    private final CommentService commentService;

    @PostMapping
    ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
    @RequestBody ItemDto itemDto) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{id}")
    ItemDto update(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId,
                   @RequestBody ItemDto itemDto) {
        return itemService.update(id, itemDto, userId);
    }

    @GetMapping("/{id}")
    ItemDtoWithBooking getById(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("getByUserId" + id + " " + " " + userId);
        return itemService.getByOwnerIdAndUserId(id, userId);
    }

    @GetMapping
    List<ItemDtoWithBooking> getAllByUserAndItemId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   Integer from,
                                                   Integer size) {

        return itemService.getAllByUserId(userId, from, size);
    }

    @GetMapping("/search")
    List<ItemDto> getByText(@RequestParam String text,
                            @RequestParam Integer from,
                            @RequestParam Integer size) {
        return itemService.getByText(text, from, size);
    }


    @PostMapping("/{itemId}/comment")
    CommentDto createComment(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody CommentDto comment) {
        log.info("Запрос на добавление коммента" + comment);
        return commentService.create(itemId, userId, comment);
    }

}
