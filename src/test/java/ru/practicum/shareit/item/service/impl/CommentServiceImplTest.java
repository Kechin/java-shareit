package ru.practicum.shareit.item.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.storage.BookingRepository;
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
    User user;
    Comment comment;

    @BeforeEach
    void before() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        commentRepository = mock(CommentRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentService = new CommentServiceImpl(commentRepository, itemRepository, bookingRepository, userRepository);
        user = new User(1L, "Sergei", "we@ddf.dd");
        item = new Item(1L, "PC", "IBM PC", true, user);
        comment = new Comment(1L, "Cool", item, user, LocalDateTime.now());
    }

    @Test
    void getAll() {
        when(commentRepository.findAll())
                .thenReturn(new ArrayList<>(Collections.singleton(comment)));
        Assertions.assertEquals(commentService.getAll(),
                new ArrayList<>(Collections.singleton(CommentMapper.toCommentDto(comment))));
    }
}