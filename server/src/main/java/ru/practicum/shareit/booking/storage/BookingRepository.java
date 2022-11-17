package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByItemIn(List<Item> items, Sort sort);

    Booking findFirstByItemIdAndEndIsBefore(Long itemId, LocalDateTime data, Sort sort);


    Booking findFirstByItemIdAndStartIsAfterOrderByEnd(Long itemId, LocalDateTime data);

    List<Booking> findBookingsByItem_IdAndBooker_Id(Long itemId, Long bookerId);

    //All
    Page<Booking> findBookingsByItem_Owner_IdOrderByStartDesc(Long ownerId, Pageable pageable);

    //Past
    Page<Booking> findBookingsByItem_Owner_IdAndEndIsBefore(Long ownerId, LocalDateTime date, Pageable pageable);

    //Future
    Page<Booking> findBookingsByItem_Owner_IdAndStartIsAfter(Long ownerId, LocalDateTime date, Pageable pageable);

    //Current
    Page<Booking>
    findBookingsByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(Long ownerId, LocalDateTime date1,
                                                             LocalDateTime date2, Pageable pageable);

    //Waiting
    //Rejected
    Page<Booking> findBookingsByItem_Owner_IdAndStatusEquals(Long ownerId, Status status, Pageable pageable);

    //ByBooker
    //All
    Page<Booking> findBookingsByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    //Past
    Page<Booking> findBookingsByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime date, Pageable pageable);

    //Future
    Page<Booking> findBookingsByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime date, Pageable pageable);

    //Current
    Page<Booking> findBookingsByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime date1,
                                                                      LocalDateTime date2, Pageable pageable);

    //Waiting
    //Rejected
    Page<Booking> findBookingsByBookerIdAndStatusEquals(Long bookerId, Status status, Pageable pageable);


}
