package ru.practicum.shareit.item.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
class ItemServiceImplTest {
    @MockBean
    ItemServiceImpl itemService;
    ItemRepository itemRepository;
    ItemRequestRepository itemRequestRepository;
    UserRepository userRepository;
    CommentRepository commentRepository;
    BookingRepository bookingRepository;

    User user = new User(1L, "d", "rt@ya.ru");
    Item item = new Item(1L, "PC", "IBM PC", true, user);

    @BeforeEach
    void before() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        commentRepository = mock(CommentRepository.class);
        userRepository.save(user);
        itemService = new ItemServiceImpl(itemRepository, itemRequestRepository, userRepository,
                commentRepository, bookingRepository);
    }

    @Test
    void create() {
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.create(ItemMapper.toItemDto(item), 0L));
        Assertions.assertEquals("неверный user ID",
                exception.getMessage());
        getUser();
        when(itemRepository.save(any()))
                .thenReturn(item);
        Assertions.assertEquals(itemService.create(ItemMapper.toItemDto(item), 1L), ItemMapper.toItemDto(item));
    }

    @Test
    void update() {
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.create(ItemMapper.toItemDto(item), 0L));
        Assertions.assertEquals("неверный user ID",
                exception.getMessage());
        getUser();
        getItem();
        when(itemRepository.save(any()))
                .thenReturn(item);
        Assertions.assertEquals(itemService.update(1L, ItemMapper.toItemDto(item), 1L), ItemMapper.toItemDto(item));

    }

    @Test
    void getByItemId() {
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        Assertions.assertEquals(itemService.getByItemId(1L), ItemMapper.itemDtoWithBooking(item));
    }

    @Test
    void getByText() {
        when(itemRepository
                .findItemsByDescriptionContainingIgnoreCaseAndAvailableIsTrueOrNameContainingIgnoreCaseAndAvailableIsTrue("IBM", "IBM"))
                .thenReturn(new ArrayList<>(Collections.singleton(item)));
        Assertions.assertEquals(itemService.getByText("IBM", 0, 2),
                new ArrayList<>(Collections.singleton(ItemMapper.toItemDto(item))));
    }

    @Test
    void getUser() {
        when(userRepository.findById(anyLong())).thenAnswer(invocationOnMock -> {
            Long userId = invocationOnMock.getArgument(0, Long.class);
            if (userId > 3) {
                throw new NotFoundException(
                        String.format("User с указанным ID не найден"));
            } else {
                return Optional.of(user);
            }
        });
    }

    @Test
    void getItem() {
        when(itemRepository.findById(anyLong())).thenAnswer(invocationOnMock -> {
            Long itemId = invocationOnMock.getArgument(0, Long.class);
            if (itemId > 3) {
                throw new NotFoundException(
                        String.format("Item с указанным ID не найден"));
            } else {
                return Optional.of(item);
            }
        });
    }

    @Test
    void getItemRequest() {
        when(itemRepository.findById(anyLong())).thenAnswer(invocationOnMock -> {
            Long itemId = invocationOnMock.getArgument(0, Long.class);
            if (itemId > 3) {
                throw new NotFoundException(
                        String.format("Item с указанным ID не найден"));
            } else {
                return Optional.of(item);
            }
        });
    }
}