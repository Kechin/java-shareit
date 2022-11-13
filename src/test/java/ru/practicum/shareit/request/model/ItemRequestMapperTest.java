package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

class ItemRequestMapperTest {
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private List<ItemRequestDto> itemRequestDtos;

    @BeforeEach
    void before() {
        itemRequest = new ItemRequest(1L, "descrip",
                LocalDateTime.now(), new User(1L, "dd", "er@df.er"));
        itemRequestDto = ItemRequestMapper.itemRequestDto(itemRequest);
        itemRequestDtos = ItemRequestMapper.itemRequestDtos(List.of(itemRequest));

    }

    @Test
    void itemRequestDto() {
        Assertions.assertEquals(itemRequestDto.getId(), itemRequest.getId());

    }

    @Test
    void itemRequestDtos() {
        Assertions.assertEquals(itemRequestDtos.get(0).getId(), itemRequest.getId());

    }
}