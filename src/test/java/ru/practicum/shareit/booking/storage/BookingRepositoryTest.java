package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired

    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;
    private User user1;
    private User user2;
    private User user3;
    private Item item1;
    private Item item2;
    private Item item3;
    private Booking booking1;
    private Booking booking2;
    private Booking booking3;
    private LocalDateTime dateTimeBefore;
    private LocalDateTime dateTimeAfter;
    private Sort sortByDescEnd;
    private final Pageable pageable = PageRequest.of(0, 3, sortByDescEnd);

    @BeforeEach
    void beforeEach() {
        sortByDescEnd = Sort.by(Sort.Direction.DESC, "end");
        dateTimeAfter = LocalDateTime.of(2022, 1, 1, 1, 1);
        dateTimeBefore = LocalDateTime.of(2026, 1, 1, 1, 1);
        user1 = new User(1l, "Sergei", "123@gmail,com");
        userRepository.save(user1);
        user2 = new User(2l, "Timofei", "223@gmail,com");
        userRepository.save(user2);
        user3 = new User(3l, "Evgenii", "323@gmail,com");
        userRepository.save(user3);
        item1 = new Item(1l, "PC", "P1,128Mb", true, user1);
        itemRepository.save(item1);
        item2 = new Item(2l, "PC", "P2,128Mb", true, user1);
        itemRepository.save(item2);
        item3 = new Item(3l, "PC", "P3,128Mb", true, user2);
        itemRepository.save(item3);
        LocalDateTime dateTime1 = LocalDateTime.of(2025, 01, 01, 0, 0);
        LocalDateTime dateTime2 = LocalDateTime.of(2025, 02, 01, 0, 0);
        LocalDateTime dateTime3 = LocalDateTime.of(2025, 03, 01, 0, 0);
        LocalDateTime dateTime4 = LocalDateTime.of(2025, 04, 01, 0, 0);


        booking1 = new Booking(1l, dateTime1, dateTime2, item1, user2, Status.WAITING);
        bookingRepository.save(booking1);
        booking2 = new Booking(2l, dateTime2, dateTime3, item2, user2, Status.WAITING);
        bookingRepository.save(booking2);
        booking3 = new Booking(3l, dateTime3, dateTime4, item3, user1, Status.WAITING);
        bookingRepository.save(booking3);

    }

    @Test
    void findFirstByItemIdAndEndIsBefore() {
        Booking actual = bookingRepository.findFirstByItemIdAndEndIsBefore(item1.getId(), dateTimeBefore, sortByDescEnd);
        Assertions.assertEquals(booking1.getId(), actual.getId());

    }

    @Test
    void findFirstByItemIdAndStartIsAfterOrderByEnd() {

        Booking actual = bookingRepository.findFirstByItemIdAndStartIsAfterOrderByEnd(item1.getId(), dateTimeAfter);
        Assertions.assertEquals(booking1.getId(), actual.getId());
    }

    @Test
    void findBookingsByItem_IdAndBooker_Id() {
        List<Booking> actual = bookingRepository.findBookingsByItem_IdAndBooker_Id(item1.getId(), user2.getId());
        Assertions.assertEquals(List.of(booking1.getId()), List.of(actual.get(0).getId()));
    }

    @Test
    void findBookingsByItem_Owner_IdOrderByStartDesc() {
        List<Booking> actual = (bookingRepository.findBookingsByItem_Owner_IdOrderByStartDesc(user2.getId(), pageable)).toList();
        Assertions.assertEquals(List.of(booking3.getId()), List.of(actual.get(0).getId()));
    }


}