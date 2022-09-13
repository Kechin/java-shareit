package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto itemDto, Long userId);

    ItemDto update(Long id, ItemDto itemDto, Long userId);

    ItemDto getById(Long id);

    List<ItemDto> getByUserId(Long userId);

    List<ItemDto> getByText(String text);
}
