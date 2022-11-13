package ru.practicum.shareit.item.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
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
        bookingRepository = mock(BookingRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
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

        ItemRequest itemReq = new ItemRequest(13L, "desc", LocalDateTime.now(), user);

        item.setItemRequest(itemReq);
        ItemDto itemDto = new ItemDto(1L, "lol", "desc", true, UserMapper.toUserDto(user),
                null, 13L);
        NotFoundException exception2 = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.create(itemDto, 1L));
        Assertions.assertEquals("ItemRequest c данным Id не найден",
                exception2.getMessage());
    }

    @Test
    void update() {

        getUser();
        getItem();
        when(itemRepository.save(any()))
                .thenReturn(item);
        Assertions.assertEquals(itemService.update(1L, ItemMapper.toItemDto(item), 1L), ItemMapper.toItemDto(item));
        item.setOwner(new User(12L, "2", "ddf@df.er"));
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.update(1L, ItemMapper.toItemDto(item), 1L));
        Assertions.assertEquals("ID пользователя не соотвествует Владельцу",
                exception.getMessage());
        ItemDto itemDto = ItemMapper.toItemDto(item);
        item.setOwner(user);
        itemDto.setName(null);
        itemDto.setDescription(null);
        itemDto.setAvailable(null);
        Assertions.assertEquals(itemService.update(1L, itemDto, 1L), ItemMapper.toItemDto(item));


    }


    @Test
    void getByItemId() {
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        Assertions.assertEquals(itemService.getByItemId(1L), ItemMapper.itemDtoWithBooking(item));
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.getByItemId(0L));
        Assertions.assertEquals("Item не найден",
                exception.getMessage());
    }

    @Test
    void getAllByUserId() {
        getUser();
        when(itemRepository.findItemsByOwnerId(any()))
                .thenReturn(new ArrayList<>(Collections.singleton(item)));
        Assertions.assertEquals(itemService.getAllByUserId(1L, 0, 2),
                new ArrayList<>(Collections.singleton(ItemMapper.itemDtoWithBooking(item))));
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
    void getByOwnerIdAndUserId() {
        getUser();
        getItem();
        when(itemRepository.findById(any()))
                .thenReturn(Optional.ofNullable(item));
        Assertions.assertEquals(itemService.getByOwnerIdAndUserId(1L, 1L),
                ItemMapper.itemDtoWithBooking(item));
    }


}