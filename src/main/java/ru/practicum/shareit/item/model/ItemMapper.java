package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.user.model.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static Item toItem(ItemDto itemDto) {

        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(),
                UserMapper.toUser(itemDto.getOwner()));
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                UserMapper.toUserDto(item.getOwner()), null);
    }

    public static ItemDtoWithBooking itemDtoWithBooking(Item item) {
        return new ItemDtoWithBooking(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                null, null, new ArrayList<>());
    }

    public static List<ItemDtoWithBooking> itemDtoWithBookings(List<Item> items) {
        return items.stream().map(ItemMapper::itemDtoWithBooking).collect(Collectors.toList());
    }

}
