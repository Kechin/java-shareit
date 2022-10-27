package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.model.BookingReqState;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto create(BookingRequestDto bookingDto, Long bookerId) {
        dataCheck(bookingDto.getStart(), bookingDto.getEnd());
        Item item = getItem(bookingDto.getItemId());
        User user = getUser(bookerId);
        log.info("Запрос на добавление " + " " + bookingDto + "" + bookerId + " " + item.getOwner());

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь была забронирована ранее");
        }
        if (item.getOwner().getId().equals(bookerId)) {
            throw new NotFoundException("Невозможно забронировать вещь с указанным ID");
        }
        Booking booking = BookingMapper.reqDtoToBooking(bookingDto, user, item);

        bookingRepository.save(booking);

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto update(Long id, Long userId, Boolean approved) {
        log.info("Попытка обновить статус бронирования");
        Booking booking = bookingRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Бронирования с указанным id не существует"));
        if (!booking.getItem().getAvailable()) {
            throw new ValidationException("Невозможно  забронировать данную вещь " + booking.getItem().getAvailable() +
                    booking.getItem().getOwner().getId() + " " + userId);
        }
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Вещь не принадлежит пользователю с данным id");
        }
        Status status = booking.getStatus();
        if (approved == null || status.equals(Status.APPROVED) && approved || status.equals(Status.REJECTED) && !approved) {
            throw new ValidationException("ОШИБКА в переданном статусе бронирования " + status);
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return BookingMapper.toBookingDto(booking);
    }



    @Override
    public List<BookingDto> getByText(String text) {
        return null;
    }

    @Override
    public Booking getById(Long id, Long userId) {
        getUser(userId);
        Booking booking = bookingRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Неверный Booking ID"));
        if (
                (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId))) {
            throw new NotFoundException("Не соответствие Id User");
        }
        return booking;
    }
    @Override
    public List<BookingDto> getAllByBooker(Long bookerId, String state) {
        getUser(bookerId);
        try {
            BookingReqState eState = BookingReqState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("{\"error\"" + ":" + "\"Unknown state: " + state + "\"}");
        }
        List<BookingDto> bookings = BookingMapper
                .bookingDtos(bookingRepository.findBookingsByBookerIdOrderByStartDesc(bookerId));
        switch (BookingReqState.valueOf(state)) {
            case ALL:
                return BookingMapper.bookingDtos(bookingRepository.findBookingsByBookerIdOrderByStartDesc( bookerId));

            case PAST:
                return BookingMapper.bookingDtos(bookingRepository
                        .findBookingsByBookerIdAndEndIsBeforeOrderByEndDesc(bookerId, LocalDateTime.now()));

            case FUTURE:
                return BookingMapper.bookingDtos(bookingRepository
                        .findBookingsByBookerIdAndStartIsAfterOrderByEndDesc(bookerId, LocalDateTime.now()));

            case CURRENT:
                return BookingMapper.bookingDtos(bookingRepository
                        .findBookingsByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc
                                (bookerId, LocalDateTime.now(), LocalDateTime.now()));
            case WAITING:
                return BookingMapper.bookingDtos(bookingRepository.findBookingsByBookerIdAndStatusEqualsOrderByEndDesc
                        (bookerId, Status.WAITING));

            case REJECTED:
                log.info("Rejected");
                return BookingMapper.bookingDtos(bookingRepository.findBookingsByBookerIdAndStatusEqualsOrderByEndDesc
                      (bookerId, Status.REJECTED));
            default:
                throw new ValidationException("");
        }


    }
    @Override
    public List<BookingDto> getAllByOwner(Long ownerId, String state) {
        getUser(ownerId);
        try {
            BookingReqState eState = BookingReqState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("{\"error\"" + ":" + "\"Unknown state: " + state + "\"}");
        }
        switch (BookingReqState.valueOf(state)) {
            case ALL:
                List<BookingDto> bookings = BookingMapper.bookingDtos(bookingRepository
                        .findBookingsByItem_Owner_IdOrderByStartDesc(ownerId));
                return bookings;
            case PAST:
                return BookingMapper.bookingDtos(bookingRepository
                        .findBookingsByItem_Owner_IdAndEndIsBeforeOrderByEndDesc(ownerId, LocalDateTime.now()));

            case FUTURE:
                return BookingMapper.bookingDtos(bookingRepository
                        .findBookingsByItem_Owner_IdAndStartIsAfterOrderByEndDesc(ownerId, LocalDateTime.now()));

            case CURRENT:
                return BookingMapper.bookingDtos(bookingRepository
                        .findBookingsByItem_Owner_IdAndStartIsBeforeAndEndIsAfterOrderByEndDesc
                                (ownerId, LocalDateTime.now(), LocalDateTime.now()));
            case WAITING:
                return BookingMapper.bookingDtos(bookingRepository
                        .findBookingsByItem_Owner_IdAndStatusEqualsOrderByEndDesc(ownerId, Status.WAITING));

            case REJECTED:
                log.info("Rejected");
                return BookingMapper.bookingDtos(bookingRepository
                        .findBookingsByItem_Owner_IdAndStatusEqualsOrderByEndDesc(ownerId, Status.REJECTED));
            default:
                throw new ValidationException("");
        }

    }


    private User getUser(Long userId)  {
        return  userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("неверный User ID"));
    }
    private Item getItem(Long itemId)  {
        return  itemRepository.findById(itemId).orElseThrow(() ->
               new NotFoundException("неверный Item ID"));
    }
    private void dataCheck(LocalDateTime start, LocalDateTime end){

        LocalDateTime now = LocalDateTime.now().minusSeconds(5L);
        if (start.isAfter(end) || start.equals(end)|| start.isBefore(now)|| end.isBefore(now)){
            throw  new ValidationException("Неверные даты");
        }
    }

}
