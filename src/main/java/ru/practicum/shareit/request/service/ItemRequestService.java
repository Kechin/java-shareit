package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto getByRequestId(Long requestId, Long requesterId);

    List<ItemRequestDto> getAll(Long userId, Integer from, Integer size);

    List<ItemRequestDto> getAllForRequester(Long requesterId);

    ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId);
}
