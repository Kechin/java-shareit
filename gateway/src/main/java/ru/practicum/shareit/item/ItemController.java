package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Collections;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;


    @PostMapping
    public ResponseEntity<Object> createItem(
            @RequestBody @Validated({Create.class}) ItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Creating item {}", itemDto);
        return itemClient.createItem(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(
            @RequestHeader("X-Sharer-User-Id") long userId, @PathVariable Long itemId) {
        log.info("Get item by userId={}", userId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getItemByUserIdAndItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Min(0) @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Min(1) @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get item By userId{}, userId={}", userId);
        return itemClient.getItemByItemIdAndUserId(userId, from, size);
    }


    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody @Validated({Update.class}) ItemDto itemDto,
                                             @PathVariable Long itemId) {
        log.info("Update item {}, itemId={}", itemId);
        return itemClient.updateItem(itemDto, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> delItem(
            @PathVariable Long itemId) {
        log.info("Delete item {}, itemId={}", itemId);
        return itemClient.delItem(itemId);
    }

    @GetMapping("/search")
    ResponseEntity<Object> getByText(@RequestParam String text,
                                     @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                     @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        if (text.isBlank()) {
            return ResponseEntity.ok().body(Collections.emptyList());
        }
        return itemClient.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable Long itemId,
                                                @RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Valid @RequestBody CommentDto commentDto) {
        log.info("Добавление комментария от: {}", userId);
        return itemClient.createComment(itemId, userId, commentDto);
    }
}
