package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;


@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    BookingDto create(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                      @RequestBody @Valid BookingRequestDto bookingDto) {
        log.info("Запрос на добавление брони " + bookingDto);
        return bookingService.create(bookingDto, bookerId);
    }

    @PatchMapping("/{id}")
    BookingDto setApprove(@PathVariable long id, @RequestHeader("X-Sharer-User-Id") Long bookerId,
                          @RequestParam Boolean approved) {


        return bookingService.update(id, bookerId, approved);
    }

    @GetMapping
    List<BookingDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @RequestParam(defaultValue = "ALL") String state,
                            @RequestParam(defaultValue = "0") @Min(0) Integer from,
                            @RequestParam(defaultValue = "10") @Min(1) Integer size) {

        return bookingService.getAllByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    List<BookingDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @RequestParam(defaultValue = "ALL") String state,
                                   @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                   @RequestParam(defaultValue = "10") @Min(1) Integer size) {

        return bookingService.getAllByOwner(userId, state, from, size);
    }

    @GetMapping("/{id}")
    BookingDto getById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable Long id) {

        return bookingService.getById(id, userId);
    }



}
