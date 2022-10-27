package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {


    Booking findFirstByItemIdAndEndIsBeforeOrderByEndDesc(Long itemId,LocalDateTime data);


    Booking findFirstByItemIdAndStartIsAfterOrderByEnd(Long itemId, LocalDateTime data);

    List<Booking> findBookingsByItem_IdAndBooker_Id(Long itemId, Long bookerId);

    //All
    List<Booking> findBookingsByItem_Owner_IdOrderByStartDesc(Long ownerId);
    //Past
    List<Booking> findBookingsByItem_Owner_IdAndEndIsBeforeOrderByEndDesc(Long ownerId,LocalDateTime date);
    //Future
    List<Booking> findBookingsByItem_Owner_IdAndStartIsAfterOrderByEndDesc(Long ownerId,LocalDateTime date);
    //Current
    List<Booking> findBookingsByItem_Owner_IdAndStartIsBeforeAndEndIsAfterOrderByEndDesc
        (Long ownerId,LocalDateTime date1,LocalDateTime date2);
    //Waiting
    //Rejected
    List<Booking> findBookingsByItem_Owner_IdAndStatusEqualsOrderByEndDesc(Long ownerId, Status status);


    //ByBooker
    //All
    List<Booking> findBookingsByBookerIdOrderByStartDesc(Long bookerId);
    //Past
    List<Booking> findBookingsByBookerIdAndEndIsBeforeOrderByEndDesc(Long bookerId,LocalDateTime date);
    //Future
    List<Booking> findBookingsByBookerIdAndStartIsAfterOrderByEndDesc(Long bookerId,LocalDateTime date);
    //Current
    List<Booking> findBookingsByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc
    (Long bookerId,LocalDateTime date1,LocalDateTime date2);
    //Waiting
    //Rejected
    List<Booking> findBookingsByBookerIdAndStatusEqualsOrderByEndDesc(Long bookerId, Status status);


}
