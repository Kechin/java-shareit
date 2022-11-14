package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@SpringBootTest
class BookingMapperTest {
    private ItemRequest itemRequest;
    private Booking booking;
    private Item item;
    private User user;
    private BookingDto bookingDto;
    private BookingShortDto bookingShortDto;
    private BookingRequestDto bookingRequestDto;

    @BeforeEach
    void before() {
        user = new User(1L, "user", "user@user.ru");
        item = new Item(1L, "item", "super item", true, user, null);
        booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, user, Status.APPROVED);
        bookingDto = BookingMapper.toBookingDto(booking);
        bookingShortDto = BookingMapper.bookingShortDto(booking);

    }


    @Test
    void toBookingDto() {
        Assertions.assertNotNull(booking);
        Assertions.assertEquals(booking.getItem().getId(), bookingDto.getItem().getId());
        Assertions.assertEquals(booking.getBooker().getId(), bookingDto.getBooker().getId());
        Assertions.assertEquals(booking.getEnd(), bookingDto.getEnd());
        Assertions.assertEquals(booking.getStart(), bookingDto.getStart());
        Assertions.assertEquals(booking.getStatus(), bookingDto.getStatus());

    }

    @Test
    void bookingShortDto() {
        Assertions.assertEquals(booking.getBooker().getId(), bookingShortDto.getBookerId());
        Assertions.assertEquals(booking.getBooker().getId(), bookingShortDto.getBookerId());
    }

}