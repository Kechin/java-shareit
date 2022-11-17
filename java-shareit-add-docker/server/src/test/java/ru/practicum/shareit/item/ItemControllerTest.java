package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentMapper;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.service.impl.CommentServiceImpl;
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.UserMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock
    private ItemServiceImpl itemService;
    @Mock
    private CommentServiceImpl commentService;
    @InjectMocks
    private ItemController controller;
    @Autowired
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;

    private UserDto userDto;
    private ItemDto itemDto;
    private ItemDtoWithBooking itemDtoWithBooking;
    private Comment comment;
    private CommentDto commentDto = new CommentDto();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

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
        comment = new Comment(1L, "comment", ItemMapper.toItem(itemDto),
                UserMapper.toUser(userDto), LocalDateTime.now());
        commentDto.setText("comment");

    }

    @Test
    void createTest() throws Exception {
        when(itemService.create(any(), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")

                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())));
    }

    @Test
    void updateTest() throws Exception {
        itemDto.setName("IBM");
        when(itemService.update(any(), any(), any()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/{id}", 1)
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is("IBM")));
    }


    @Test
    void getByText() throws Exception {

        when(itemService.getByText(any(), any(), any())).thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .param("text", "")
                        .param("from", "0")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        mvc.perform(get("/items/search")
                        .param("text", "PC")
                        .param("from", "0")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto))));


    }

    @Test
    void getAllByUserAndItemId() throws Exception {

        when(itemService.getAllByUserId(any(), any(), any())).thenReturn(List.of(itemDtoWithBooking));

        mvc.perform(get("/items/")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "0")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDtoWithBooking))));


    }

    @Test
    void getById() throws Exception {

        when(itemService.getByOwnerIdAndUserId(any(), any())).thenReturn((itemDtoWithBooking));

        mvc.perform(get("/items/{id}", 1)
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDtoWithBooking)));


    }

    @Test
    void createComments() throws Exception {

        when(commentService.create(any(), any(), any())).thenReturn(CommentMapper.toCommentDto(comment));

        mvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString((commentDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }


}

