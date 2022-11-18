package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestMapper;
import ru.practicum.shareit.request.service.impl.ItemRequestServiceImpl;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemRequestServiceTest {
    ItemRequestService itemRequestService;
    ItemRepository itemRepository;
    ItemRequestRepository itemRequestRepository;
    UserRepository userRepository;
    CommentRepository commentRepository;
    BookingRepository bookingRepository;
    Item item;
    User user;
    User owner;
    Comment comment;
    Booking booking;
    ItemRequest itemRequest;

    @BeforeEach
    void before() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        commentRepository = mock(CommentRepository.class);
        bookingRepository = mock(BookingRepository.class);
        itemRequestService =
                new ItemRequestServiceImpl(itemRequestRepository, itemRepository, userRepository, bookingRepository);
        user = new User(1L, "Sergei", "we@ddf.dd");
        owner = new User(2L, "Owner", "re@df.dd");
        comment = new Comment(1L, "Cool", item, user, LocalDateTime.now());
        booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1L), item, user, Status.APPROVED);
        itemRequest = new ItemRequest(1L, "REQUEST", LocalDateTime.now(), user);
        item = new Item(1L, "PC", "IBM PC", true, user, itemRequest);

    }


    @Test
    void getByRequestId() {
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getByRequestId(1L, 12L));
        Assertions.assertEquals("неверный User ID",
                exception.getMessage());
        getUser();
        NotFoundException exception2 = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getByRequestId(13L, 1L));
        Assertions.assertEquals(" Неверный Itemrequest ID",
                exception2.getMessage());
        when(itemRequestRepository.findById(1L))
                .thenReturn(Optional.of(itemRequest));
        Assertions.assertEquals(itemRequestService.getByRequestId(1L, 1L).getId(),
                (itemRequest.getId()));


    }

    @Test
    void getAll() {
        getUser();
        when(itemRequestRepository.findAllByRequesterIdNot(any(), any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(itemRequest)));
        Assertions.assertEquals(new ArrayList<>(List.of(ItemRequestMapper.itemRequestDto(itemRequest))).get(0).getId(),
                (itemRequestService.getAll(1L, 0, 2)).get(0).getId());
    }

    @Test
    void getAllForRequester() {


        getUser();
        when(itemRequestRepository.findAllByUserId(any()))
                .thenReturn(Collections.singletonList(itemRequest));

        when(itemRepository.findAllByItemRequestIn(any())).thenReturn(Collections.singletonList(item));
        Assertions.assertEquals(new ArrayList<>(List.of(ItemRequestMapper.itemRequestDto(itemRequest))).get(0).getId(),
                (itemRequestService.getAllForRequester(1L)).get(0).getId());
    }

    @Test
    void create() {
        getUser();
        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest);
        Assertions.assertEquals(itemRequestService.create(ItemRequestMapper.itemRequestDto(itemRequest), 1L),
                ItemRequestMapper.itemRequestDto(itemRequest));
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
}