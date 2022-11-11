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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock
    private ItemServiceImpl itemService;

    @InjectMocks
    private ItemController controller;
    @Autowired
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;

    private UserDto userDto;
    private ItemDto itemDto;
    private ItemDtoWithBooking itemDtoWithBooking;

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
    }

    @Test
    void createTest() throws Exception {
        when(itemService.create(any(), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDto)).characterEncoding(StandardCharsets.UTF_8)
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

        mvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is("IBM")));
    }


}