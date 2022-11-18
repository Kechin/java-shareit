package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ItemRequestDto {
    private Long id;
    @NotBlank
    @NotNull
    private String description;
    private UserDto requester;
    private LocalDateTime created;
    private List<ItemDtoWithBooking> items;
}
