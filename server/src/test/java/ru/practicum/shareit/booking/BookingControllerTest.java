package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @MockBean
    private BookingService bookingService;

    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mvc;

    private UserDto userDto;
    private ItemDto itemDto;
    private ItemDtoWithBooking itemDtoWithBooking;
    private Comment comment;
    private CommentDto commentDto = new CommentDto();
    private Booking booking;
    private BookingDto bookingDto;
    private BookingRequestDto bookingRequestDto;
    private User booker;
    private Item item;
    private User owner;

    @BeforeEach
    void setUp() {


        userDto = new UserDto(
                1L,
                "sergei",
                "ser@mail.com"
        );
        itemDto = new ItemDto(
                1L,
                "PC",
                " Ibm Pc",
                true,
                userDto, null, null);

        itemDtoWithBooking = new ItemDtoWithBooking(1L,
                "PC",
                " Ibm Pc",
                true,
                null, null, null, null);
        booker = new User(1L, "user", "user@hj.dd");
        owner = new User(2L, "owner", "userd@hj.dd");
        comment = new Comment(1L, "comment", ItemMapper.toItem(itemDto),
                UserMapper.toUser(userDto), LocalDateTime.now());
        commentDto.setText("comment");
        item = new Item(1L, "PC", "description", true, owner, null);
        booking = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(11), item, booker, Status.WAITING);
        bookingDto = BookingMapper.toBookingDto(booking);
        bookingRequestDto = BookingMapper.bookingRequestDto(booking);
    }

    @Test
    void create() throws Exception {
        when(bookingService.create(any(), any()))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings/")
                        .content(objectMapper.writeValueAsString(bookingRequestDto))

                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDto)));

    }

    @Test
    void setApprove() throws Exception {
        when(bookingService.update(1L, 1L, true))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{id}", 1)
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDto)));


    }

    @Test
    void getAll() throws Exception {
        when(bookingService.getAllByBooker(any(), any(), any(), any()))
                .thenReturn(List.of(bookingDto));


        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(bookingDto))));
    }

    @Test
    void getAllByOwner() throws Exception {
        when(bookingService.getAllByOwner(any(), any(), any(), any()))
                .thenReturn(List.of(bookingDto));


        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(bookingDto))));


    }

    @Test
    void getById() throws Exception {
        when(bookingService.getById(any(), any()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/{id}/", 1)
                        .header("X-Sharer-User-Id", "1")

                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDto)));

    }
}