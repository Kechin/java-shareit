package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto itemDto, Long userId) throws Throwable;

    ItemDto update(Long id, ItemDto itemDto, Long userId);


    ItemDtoWithBooking getByItemId(Long itemId);

    ItemDtoWithBooking getByOwnerIdAndUserId(Long itemId, Long userId) ;


    List<ItemDto> getByText(String text);
    List<ItemDto> getAllByUserId(Long userId);
}
