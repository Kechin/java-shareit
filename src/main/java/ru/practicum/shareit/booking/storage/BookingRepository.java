package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {


    Booking findFirstByItemIdAndEndIsBefore(Long itemId, LocalDateTime data, Sort sort);


    Booking findFirstByItemIdAndStartIsAfterOrderByEnd(Long itemId, LocalDateTime data);

    List<Booking> findBookingsByItem_IdAndBooker_Id(Long itemId, Long bookerId);

    //All
    List<Booking> findBookingsByItem_Owner_IdOrderByStartDesc(Long ownerId);

    //Past
    List<Booking> findBookingsByItem_Owner_IdAndEndIsBefore(Long ownerId, LocalDateTime date, Sort sort);

    //Future
    List<Booking> findBookingsByItem_Owner_IdAndStartIsAfter(Long ownerId, LocalDateTime date, Sort sort);

    //Current
    List<Booking>
    findBookingsByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(Long ownerId, LocalDateTime date1,
                                                             LocalDateTime date2, Sort sort);

    //Waiting
    //Rejected
    List<Booking> findBookingsByItem_Owner_IdAndStatusEquals(Long ownerId, Status status, Sort sort);


    //ByBooker
    //All
    List<Booking> findBookingsByBookerIdOrderByStartDesc(Long bookerId);

    //Past
    List<Booking> findBookingsByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime date, Sort sort);

    //Future
    List<Booking> findBookingsByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime date, Sort sort);

    //Current
    List<Booking> findBookingsByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime date1,
                                                                      LocalDateTime date2, Sort sort);

    //Waiting
    //Rejected
    List<Booking> findBookingsByBookerIdAndStatusEquals(Long bookerId, Status status, Sort sort);


}
