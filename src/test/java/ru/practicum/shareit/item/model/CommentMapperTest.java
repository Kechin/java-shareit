package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@SpringBootTest
class CommentMapperTest {
    Comment comment;
    CommentDto commentDto;
    Item item;
    User user;

    @BeforeEach
    void before() {
        user = new User(1L, "user", "u@ser.r");
        item = new Item(1L, "item", "descr", true, user, null);
        comment = new Comment(1L, "text", item, user, LocalDateTime.now());
        commentDto = CommentMapper.toCommentDto(comment);
    }

    @Test
    void toCommentDto() {
        Assertions.assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName());

    }
}