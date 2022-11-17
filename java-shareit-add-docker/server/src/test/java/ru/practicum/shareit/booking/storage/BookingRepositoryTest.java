package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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
    final Pageable pageable = Pageable.unpaged();

    Sort sortByDescEnd = Sort.by(Sort.Direction.DESC, "end");
    LocalDateTime dateTimeAfter = LocalDateTime.of(2022, 1, 1, 1, 1);
    LocalDateTime dateTimeBefore = LocalDateTime.of(2026, 1, 1, 1, 1);

    User user1;
    User user2;
    User user3;
    Item item1;
    Item item2;
    Item item3;

    Booking booking1;
    Booking booking2;
    Booking booking3;

    @BeforeEach
    void before() {
        LocalDateTime dateTime1 = LocalDateTime.of(2025, 01, 01, 0, 0);
        LocalDateTime dateTime2 = LocalDateTime.of(2025, 02, 01, 0, 0);
        LocalDateTime dateTime3 = LocalDateTime.of(2025, 03, 01, 0, 0);
        LocalDateTime dateTime4 = LocalDateTime.of(2025, 04, 01, 0, 0);
        user1 = userRepository.save(new User(1L, "Sergei", "123@gmail,com"));
        user2 = userRepository.save(new User(2L, "Timofei", "223@gmail,com"));
        user3 = userRepository.save(new User(3L, "Evgenii", "323@gmail,com"));
        item1 = itemRepository.save(new Item(1L, "PC", "P1,128Mb", true, user1));
        item2 = itemRepository.save(new Item(2L, "PC", "P2,128Mb", true, user1));
        item3 = itemRepository.save(new Item(3L, "PC", "P3,128Mb", true, user2));
        booking1 = bookingRepository.save(new Booking(1L, dateTime1, dateTime2, item1, user2, Status.WAITING));
        booking2 = bookingRepository.save(new Booking(2L, dateTime2, dateTime3, item2, user2, Status.APPROVED));
        booking3 = bookingRepository.save(new Booking(3L, dateTime3, dateTime4, item3, user1, Status.WAITING));

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


    @Test
    void findBookingsByItem_Owner_IdAndStatusEquals() {
        List<Booking> actual = bookingRepository
                .findBookingsByItem_Owner_IdAndStatusEquals(user1.getId(), Status.APPROVED, pageable).toList();
        Assertions.assertEquals(List.of(booking2.getId()), List.of(actual.get(0).getId()));

    }


    @AfterEach
    void after() {

        userRepository.deleteAll();


    }

}