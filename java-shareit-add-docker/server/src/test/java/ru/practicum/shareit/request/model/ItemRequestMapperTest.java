package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
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
        Assertions.assertNotNull(itemRequestDto);
        Assertions.assertEquals(itemRequestDto.getId(), itemRequest.getId());
        Assertions.assertEquals(itemRequestDto.getDescription(), itemRequest.getDescription());
        Assertions.assertEquals(itemRequestDto.getCreated(), itemRequest.getCreated());
        Assertions.assertEquals(itemRequestDto.getRequester(), UserMapper.toUserDto(itemRequest.getRequester()));

    }

    @Test
    void itemRequestDtos() {
        Assertions.assertNotNull(itemRequestDtos.get(0));

        Assertions.assertEquals(itemRequestDtos.get(0).getId(), itemRequest.getId());
        Assertions.assertEquals(itemRequestDtos.get(0).getDescription(), itemRequest.getDescription());
        Assertions.assertEquals(itemRequestDtos.get(0).getCreated(), itemRequest.getCreated());
        Assertions.assertEquals(itemRequestDtos.get(0).getRequester(), UserMapper.toUserDto(itemRequest.getRequester()));

    }

}