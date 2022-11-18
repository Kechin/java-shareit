package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor

public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody ItemRequestDto itemRequestDto) {

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

        return itemRequestService.getAll(userId, from, size);
    }

    @GetMapping()
    List<ItemRequestDto> getAllForRequester(@RequestHeader("X-Sharer-User-Id") Long requesterId) {
        log.info("Запрос на  получение всех реквестов для пользователя");
        return itemRequestService.getAllForRequester(requesterId);
    }
}
