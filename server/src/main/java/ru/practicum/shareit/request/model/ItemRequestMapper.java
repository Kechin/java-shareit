package ru.practicum.shareit.request.model;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequestDto itemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(),
                UserMapper.toUserDto(itemRequest.getRequester()), itemRequest.getCreated(), null);
    }

    public static List<ItemRequestDto> itemRequestDtos(List<ItemRequest> itemRequests) {
        return itemRequests.stream().map(ItemRequestMapper::itemRequestDto).collect(Collectors.toList());
    }
}
