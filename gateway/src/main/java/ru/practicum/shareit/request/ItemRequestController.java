package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
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
                                  @Valid @RequestBody ItemRequestDto itemRequestDto) {

        return requestClient.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping("/{requestId}")
    ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        return requestClient.getItemRequest(requestId, userId);
    }

    @GetMapping("/all")
    ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                  @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {
        if (from == null || size == null) {
            // Оставил т.к. нужно возвращать пустой список
            return ResponseEntity.ok().body(Collections.emptyList());
        }
        return requestClient.getAll(userId, from, size);
    }

    @GetMapping
    ResponseEntity<Object> getAllForRequester(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на  получение всех реквестов для пользователя");
        return requestClient.getAllforRequester(userId);
    }
}
