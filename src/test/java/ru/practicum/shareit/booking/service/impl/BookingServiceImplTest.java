package ru.practicum.shareit.booking.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;

import static org.mockito.Mockito.mock;

class BookingServiceImplTest {

    BookingServiceImpl bookingService;
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

    @BeforeEach
    void before() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        commentRepository = mock(CommentRepository.class);
        bookingRepository = mock(BookingRepository.class);
        bookingService = new BookingServiceImpl(itemRepository, bookingRepository, userRepository);
        user = new User(1L, "Sergei", "we@ddf.dd");
        owner = new User(2L, "Owner", "re@df.dd");
        item = new Item(1L, "PC", "IBM PC", true, owner);
        comment = new Comment(1L, "Cool", item, user, LocalDateTime.now());
        booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().minusMonths(1L), item, user, Status.APPROVED);
    }


    @Test
    void getAllByOwner() {
    }
}