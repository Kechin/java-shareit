package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;


    @Transactional
    @Override
    public CommentDto create(Long itemId, Long userId, CommentDto commentText) {
        log.info("Запрос на добавление коммента " + userId + " " + commentText);
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("неверный User ID"));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("неверный Item ID"));
        bookingRepository.findBookingsByItem_IdAndBooker_Id(itemId, userId).stream()
                .filter(v -> v.getEnd().isBefore(LocalDateTime.now()))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Данный пользователь вещь не использовал."));
        Comment newComment = new Comment(null, commentText.getText(), item, user, LocalDateTime.now());

        return CommentMapper.toCommentDto(commentRepository.save(newComment));
    }

    @Override
    public List<CommentDto> getAll() {
        return CommentMapper.toCommentDtos(commentRepository.findAll());
    }


}
