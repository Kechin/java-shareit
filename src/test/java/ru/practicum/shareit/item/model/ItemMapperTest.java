package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

@SpringBootTest
class ItemMapperTest {
    private ItemRequest itemRequest;
    private Booking booking;
    private Item item;
    private ItemDto itemDto;
    private User user;

    @BeforeEach
    void before() {

        user = new User(1L, "user", "user@user.ru");

        itemDto = new ItemDto(1L, "item", "super item", true, UserMapper.toUserDto(user),
                null, null);
        item = ItemMapper.toItem(itemDto);
    }

    @Test
    void toItem() {
        Assertions.assertEquals(item.getId(), itemDto.getId());

    }

    @Test
    void toItemDto() {
        Assertions.assertEquals(ItemMapper.toItemDto(item).getId(), itemDto.getId());

    }
}