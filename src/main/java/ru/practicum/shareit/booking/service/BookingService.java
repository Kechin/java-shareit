package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {


    BookingDto create(BookingRequestDto bookingDto, Long ownerId);

    BookingDto update(Long id, Long userId, Boolean approve);


    List<BookingDto> getAllByBooker(Long userId, String state, Integer from, Integer size);


    BookingDto getById(Long id, Long userId);

    List<BookingDto> getAllByOwner(Long userId, String state, Integer from, Integer size);
}
