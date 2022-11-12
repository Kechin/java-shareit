package ru.practicum.shareit.booking.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Comment;
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
        booking = new Booking(1L, LocalDateTime.now().plusDays(4L), LocalDateTime.now().plusDays(10L), item, user, Status.APPROVED);
    }

    @Test
    void create() {
        getItem();

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.create(BookingMapper.bookingRequestDto(booking), 0L));
        Assertions.assertEquals("неверный User ID",
                exception.getMessage());
        getUser();
        when(bookingRepository.save(any()))
                .thenReturn(booking);
        Assertions.assertEquals(bookingService.create(BookingMapper.bookingRequestDto(booking), 1L).getBooker(),
                BookingMapper.toBookingDto(booking).getBooker());
    }

    @Test
    void update() {
        getItem();
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.create(BookingMapper.bookingRequestDto(booking), 0L));
        Assertions.assertEquals("неверный User ID",
                exception.getMessage());
        getUser();
        getBooker();
        when(bookingRepository.save(any()))
                .thenReturn(booking);
        Assertions.assertEquals(bookingService.update(1L, 2L, false).getBooker(),
                BookingMapper.toBookingDto(booking).getBooker());
    }

    @Test
    void getById() {
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.create(BookingMapper.bookingRequestDto(booking), 0L));
        Assertions.assertEquals("неверный Item ID",
                exception.getMessage());
        getUser();
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        Assertions.assertEquals(bookingService.getById(1L, 1L), booking);
    }

    //    serValidation();
//    when(itemRepository.findByOwnerId(any(), any()))
//            .thenReturn(new PageImpl<>(Collections.singletonList(item1)));
//    when(mapper.toInfoItemDto(any())).thenReturn(infoItemDto1);
//        Assertions.assertEquals(new ArrayList<>(List.of(infoItemDto1)),
//            itemService.getAllItemsByOwnerId(1L, PageRequest.of(0, 10)));
//}
    @Test
    void getAllByBooker() {
        getUser();
        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.getAllByBooker(1L, "ALLP", 0, 2));
        Assertions.assertEquals("{\"error\":\"Unknown state: ALLP\"}",
                exception.getMessage());

        when(bookingRepository.findBookingsByBookerIdOrderByStartDesc(any(), any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));
        Assertions.assertEquals(new ArrayList<>(List.of(BookingMapper.toBookingDto(booking))),
                bookingService.getAllByBooker(1L, "ALL", 0, 2));

    }

    @Test
    void getAllByOwner() {
        getUser();
        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.getAllByOwner(1L, "ALLP", 0, 2));
        Assertions.assertEquals("{\"error\":\"Unknown state: ALLP\"}",
                exception.getMessage());
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
    void getBooker() {
        when(bookingRepository.findById(anyLong())).thenAnswer(invocationOnMock -> {
            Long bookingId = invocationOnMock.getArgument(0, Long.class);
            if (bookingId > 3) {
                throw new NotFoundException(
                        String.format("Booking с указанным ID не найден"));
            } else {
                return Optional.of(booking);
            }
        });
    }

    @Test
    void findBookingsByItem_Owner_IdOrderByStartDesc() {
        when(bookingRepository.findBookingsByItem_Owner_IdOrderByStartDesc(anyLong(), any()))
                .thenAnswer(invocationOnMock -> {
                    Long bookingId = invocationOnMock.getArgument(0, Long.class);
                    if (bookingId > 3) {
                        throw new NotFoundException(
                                String.format("Booking с указанным ID не найден"));
                    } else {
                        return Optional.of(booking);
                    }
                });
    }
}
