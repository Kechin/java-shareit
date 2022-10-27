package ru.practicum.shareit.booking.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static Booking toBooking(BookingDto bookingDto) {
        User user=(bookingDto.getBooker());
        Item item =bookingDto.getItem();
        log.info(bookingDto.toString());
        Booking booking = new Booking(bookingDto.getId(),bookingDto.getStart(),bookingDto.getEnd(),
                item,user, bookingDto.getStatus() );
        log.info("попытка создать Booking "+ booking);
        return booking;
    }
    public static Booking reqDtoToBooking(BookingRequestDto bookingDto,User user, Item item) {

        log.info(bookingDto.toString());
        Booking booking = new Booking(null,bookingDto.getStart(),bookingDto.getEnd(),
                item,user, Status.WAITING);
        log.info("попытка создать Booking "+ booking);
        return booking;
    }


    public static BookingDto toBookingDto(Booking booking) {
        log.info("попытка создать BookingDto "+ booking);
        return new BookingDto(booking.getId(), booking.getStart(),booking.getEnd(),booking.getItem(),
                booking.getBooker(),booking.getStatus());
    }
    public static BookingShortDto bookingShortDto(Booking booking){
        return new BookingShortDto(booking.getId(),booking.getBooker().getId());
    }
    public static List<BookingDto> bookingDtos(List<Booking> bookings) {
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }
}
