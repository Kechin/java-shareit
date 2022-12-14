package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto create(Long itemId, Long userId, CommentDto comment);

    List<CommentDto> getAll();
}
