package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
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
    ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @Validated({Create.class})
    @RequestBody ItemDto itemDto) throws Throwable {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{id}")
    ItemDto update(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId,
                   @Validated({Update.class}) @RequestBody ItemDto itemDto) {
        return itemService.update(id, itemDto, userId);
    }

    @GetMapping("/{id}")
    ItemDtoWithBooking getById(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("getByUserId" + id + " " + " " + userId);
        return itemService.getByOwnerIdAndUserId(id, userId);
    }

    @GetMapping
    List<ItemDtoWithBooking> getAllByUserAndItemId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("getAllByUserId");
        return itemService.getAllByUserId(userId);
    }

    @GetMapping("/search")
    List<ItemDto> getByText(@RequestParam String text) {

        return itemService.getByText(text);
    }


    @PostMapping("/{itemId}/comment")
    CommentDto createComment(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                             @Valid @RequestBody CommentDto comment) throws Throwable {
        log.info("Запрос на добавление коммента" + comment);
        return commentService.create(itemId, userId, comment);
    }

    @GetMapping("/allcomment")
    List<CommentDto> getComments() {
        return commentService.getAll();
    }
}
