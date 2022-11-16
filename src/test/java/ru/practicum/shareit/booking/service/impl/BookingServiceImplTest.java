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
        booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(2L), item, user, Status.WAITING);

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


        NotFoundException exception2 = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.create(BookingMapper.bookingRequestDto(booking), 2L));
        Assertions.assertEquals("Невозможно забронировать вещь с указанным ID",
                exception2.getMessage());


        item.setAvailable(false);
        booking.setItem(item);
        ValidationException exception3 = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.create(BookingMapper.bookingRequestDto(booking), 1L));
        Assertions.assertEquals("Вещь была забронирована ранее",
                exception3.getMessage());
        item.setAvailable(true);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(200));
        ValidationException exception4 = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.create(BookingMapper.bookingRequestDto(booking), 1L));
        Assertions.assertEquals("Неверные даты",
                exception4.getMessage());


    }

    @Test
    void update() {
        NotFoundException exception5 = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.update(33L, 1L, true));
        Assertions.assertEquals("Бронирования с указанным id не существует",
                exception5.getMessage());
        getItem();
        getUser();
        getBooker();

        ValidationException exception4 = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.update(1L, 2L, null));
        Assertions.assertEquals("ОШИБКА в переданном статусе бронирования WAITING",
                exception4.getMessage());


        when(bookingRepository.save(any()))
                .thenReturn(booking);
        Assertions.assertEquals(bookingService.update(1L, 2L, false).getBooker(),
                BookingMapper.toBookingDto(booking).getBooker());
        when(bookingRepository.save(any()))
                .thenReturn(booking);
        Assertions.assertEquals(bookingService.update(1L, 2L, true).getBooker(),
                BookingMapper.toBookingDto(booking).getBooker());
        NotFoundException exception2 = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.update(3L, 1L, true));
        Assertions.assertEquals("Вещь не принадлежит пользователю с данным id",
                exception2.getMessage());

        item.setAvailable(false);
        booking.setItem(item);
        ValidationException exception3 = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.update(1L, 1L, true));
        Assertions.assertEquals("Невозможно  забронировать данную вещь",
                exception3.getMessage());


    }

    @Test
    void getById() {
        getUser();


        NotFoundException exception5 = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getById(12L, 2L));
        Assertions.assertEquals("Неверный Booking ID",
                exception5.getMessage());
        getBooker();

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        Assertions.assertEquals(bookingService.getById(1L, 1L), BookingMapper.toBookingDto(booking));
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getById(1L, 12L));
        Assertions.assertEquals("User с указанным ID не найден",
                exception.getMessage());

    }


    @Test
    void getAllByBooker() {
        getUser();
        getItem();
        getBooker();
        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.getAllByBooker(1L, "ALLP", 0, 2));
        Assertions.assertEquals("{\"error\":\"Unknown state: ALLP\"}",
                exception.getMessage());

        when(bookingRepository.findBookingsByBookerIdOrderByStartDesc(any(), any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));
        Assertions.assertEquals(new ArrayList<>(List.of(BookingMapper.toBookingDto(booking))),
                bookingService.getAllByBooker(2L, "ALL", 0, 2));
        //past
        booking.setEnd(LocalDateTime.now().minusDays(100));
        when(bookingRepository.findBookingsByBookerIdAndEndIsBefore(any(), any(), any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));
        Assertions.assertEquals(new ArrayList<>(List.of(BookingMapper.toBookingDto(booking))),
                bookingService.getAllByBooker(2L, "PAST", 0, 2));


        //future
        booking.setStart(LocalDateTime.now().plusDays(100));
        when(bookingRepository.findBookingsByBookerIdAndStartIsAfter(any(), any(), any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));
        Assertions.assertEquals(new ArrayList<>(List.of(BookingMapper.toBookingDto(booking))),
                bookingService.getAllByBooker(2L, "FUTURE", 0, 2));


        //current
        booking.setStart(LocalDateTime.now().minusDays(1));
        when(bookingRepository.findBookingsByBookerIdAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));
        Assertions.assertEquals(new ArrayList<>(List.of(BookingMapper.toBookingDto(booking))),
                bookingService.getAllByBooker(2L, "CURRENT", 0, 2));


        //waiting
        when(bookingRepository.findBookingsByBookerIdAndStatusEquals(any(), any(), any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        Assertions.assertEquals(new ArrayList<>(List.of(BookingMapper.toBookingDto(booking))),
                bookingService.getAllByBooker(2L, "WAITING", 0, 2));
        //rejected
        booking.setStatus(Status.REJECTED);
        when(bookingRepository.findBookingsByBookerIdAndStatusEquals(any(), any(), any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        Assertions.assertEquals(new ArrayList<>(List.of(BookingMapper.toBookingDto(booking))),
                bookingService.getAllByBooker(2L, "REJECTED", 0, 2));
    }

    @Test
    void getAllByOwner() {
        getUser();
        when(bookingRepository.findBookingsByItem_Owner_IdOrderByStartDesc(any(), any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));
        Assertions.assertEquals(new ArrayList<>(List.of(BookingMapper.toBookingDto(booking))),
                bookingService.getAllByOwner(2L, "ALL", 0, 2));
        //past
        booking.setStart(LocalDateTime.now().minusDays(100));
        when(bookingRepository.findBookingsByItem_Owner_IdAndEndIsBefore(any(), any(), any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));
        Assertions.assertEquals(new ArrayList<>(List.of(BookingMapper.toBookingDto(booking))),
                bookingService.getAllByOwner(2L, "PAST", 0, 2));

        //future
        booking.setEnd(LocalDateTime.now().minusDays(100));
        when(bookingRepository.findBookingsByItem_Owner_IdAndStartIsAfter(any(), any(), any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));
        Assertions.assertEquals(new ArrayList<>(List.of(BookingMapper.toBookingDto(booking))),
                bookingService.getAllByOwner(2L, "FUTURE", 0, 2));

        //current
        booking.setEnd(LocalDateTime.now().minusDays(100));
        when(bookingRepository.findBookingsByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));
        Assertions.assertEquals(new ArrayList<>(List.of(BookingMapper.toBookingDto(booking))),
                bookingService.getAllByOwner(2L, "CURRENT", 0, 2));

        //waiting
        booking.setEnd(LocalDateTime.now().minusDays(100));
        when(bookingRepository.findBookingsByItem_Owner_IdAndStatusEquals(any(), any(), any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));
        Assertions.assertEquals(new ArrayList<>(List.of(BookingMapper.toBookingDto(booking))),
                bookingService.getAllByOwner(2L, "WAITING", 0, 2));

        //rejected
        booking.setEnd(LocalDateTime.now().minusDays(100));
        when(bookingRepository.findBookingsByItem_Owner_IdAndStatusEquals(any(), any(), any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));
        Assertions.assertEquals(new ArrayList<>(List.of(BookingMapper.toBookingDto(booking))),
                bookingService.getAllByOwner(2L, "REJECTED", 0, 2));

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


}
