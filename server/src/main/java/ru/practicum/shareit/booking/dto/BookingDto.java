package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDto item;
    private UserDto booker;
    @Enumerated(EnumType.STRING)
    private Status status;

}

