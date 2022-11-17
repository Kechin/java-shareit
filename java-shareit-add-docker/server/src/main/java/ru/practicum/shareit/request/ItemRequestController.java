package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collections;
import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody ItemRequestDto itemRequestDto) {
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            throw new ValidationException("Нельзя создать запрос без описания.");
        }
        return itemRequestService.create(itemRequestDto, userId);
    }

    @GetMapping("/{requestId}")
    ItemRequestDto getById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        return itemRequestService.getByRequestId(requestId, userId);
    }

    @GetMapping("/all")
    List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                @RequestParam(required = false) Integer from,
                                @RequestParam(required = false) Integer size) {
        if (from == null || size == null) { // Оставил т.к. нужно возвращать пустой список
            return Collections.emptyList();
        }
        if (from < 0 || size < 1) {
            throw new ValidationException("Параметры заданы from и size неверно");
        }
        return itemRequestService.getAll(userId, from, size);
    }

    @GetMapping()
    List<ItemRequestDto> getAllForRequester(@RequestHeader("X-Sharer-User-Id") Long requesterId) {
        log.info("Запрос на  получение всех реквестов для пользователя");
        return itemRequestService.getAllForRequester(requesterId);
    }
}
