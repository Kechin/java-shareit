package ru.practicum.shareit.item.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
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

class CommentServiceImplTest {

    CommentServiceImpl commentService;
    ItemRepository itemRepository;
    ItemRequestRepository itemRequestRepository;
    UserRepository userRepository;
    CommentRepository commentRepository;
    BookingRepository bookingRepository;
    Item item;
    Item item2;
    User user;
    User owner;
    Comment comment;
    Booking booking;


    @BeforeEach
    void before() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        commentRepository = mock(CommentRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentService = new CommentServiceImpl(commentRepository, itemRepository, bookingRepository, userRepository);
        user = new User(1L, "Sergei", "we@ddf.dd");
        owner = new User(2L, "owner", "wde@ddf.dd");

        item = new Item(1L, "PC", "IBM PC", true, owner);
        item2 = new Item(2L, "PC", "IBM PC", true, owner);
        comment = new Comment(1L, "Cool", item, user, LocalDateTime.now().plusDays(12));
        booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, user, Status.APPROVED);
    }

    @Test
    void create() {
        when(bookingRepository.findBookingsByItem_IdAndBooker_Id(any(), any())).thenReturn(List.of(booking));

        getItem();

        when(commentRepository.save(comment))
                .thenReturn(comment);

        new ArrayList<>(Collections.singleton(CommentMapper.toCommentDto(comment)));
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> commentService.create(2L, 1L, CommentMapper.toCommentDto(comment)));
        Assertions.assertEquals("неверный User ID",
                exception.getMessage());
    }

    @Test
    void getAll() {
        when(commentRepository.findAll())
                .thenReturn(new ArrayList<>(Collections.singleton(comment)));
        Assertions.assertEquals(commentService.getAll(),
                new ArrayList<>(Collections.singleton(CommentMapper.toCommentDto(comment))));
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
    void getComment() {
        when(itemRepository.findById(anyLong())).thenAnswer(invocationOnMock -> {
            Long itemId = invocationOnMock.getArgument(0, Long.class);
            if (itemId > 3) {
                throw new NotFoundException(
                        String.format("Comment с указанным ID не найден"));
            } else {
                return Optional.of(item);
            }
        });
    }
}