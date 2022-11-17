package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collections;


@Controller
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final RequestClient requestClient;

    @PostMapping
    ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @RequestBody ItemRequestDto itemRequestDto) {
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            throw new ValidationException("Нельзя создать запрос без описания.");
        }
        return requestClient.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping("/{requestId}")
    ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        return requestClient.getItemRequest(requestId, userId);
    }

    @GetMapping("/all")
    ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @RequestParam(required = false) Integer from,
                                  @RequestParam(required = false) Integer size) {
        if (from == null || size == null) { // Оставил т.к. нужно возвращать пустой список
            return ResponseEntity.ok().body(Collections.emptyList());
        }
        if (from < 0 || size < 1) {
            throw new ValidationException("Параметры заданы from и size неверно");
        }
        return requestClient.getAll(userId, from, size);
    }

    @GetMapping
    ResponseEntity<Object> getAllForRequester(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на  получение всех реквестов для пользователя");
        return requestClient.getAllforRequester(userId);
    }
}
