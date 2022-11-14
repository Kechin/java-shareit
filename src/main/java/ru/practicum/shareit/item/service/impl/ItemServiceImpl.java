package ru.practicum.shareit.user.service.impl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;
    @Autowired
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final Sort sortByDescEnd = Sort.by(Sort.Direction.DESC, "end");


    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, ItemRequestRepository itemRequestRepository,
                           UserRepository userRepository, CommentRepository commentRepository,
                           BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;

    }

    @Transactional
    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        log.info("Запрос на добавление " + userId + " " + itemDto);
        itemDto.setOwner(UserMapper.toUserDto(getUser(userId)));
        Item newItem = ItemMapper.toItem(itemDto);
        Long itemRequestId = itemDto.getRequestId();
        if (itemRequestId != null) {
            ItemRequest itemRequest = ((itemRequestRepository.findById(itemRequestId))
                    .orElseThrow(() -> new NotFoundException("ItemRequest c данным Id не найден")));
            newItem.setItemRequest(itemRequest);
        }

        return ItemMapper.toItemDto(itemRepository.save(newItem));
    }

    @Transactional
    @Override
    public ItemDto update(Long id, ItemDto itemDto, Long userId) {
        log.info("Запрос на обновление " + userId + " " + itemDto);
        User user = getUser(userId);
        Item oldItem = getItem(id);
        if (!userId.equals(oldItem.getOwner().getId())) {
            System.out.println("id old" + getItem(id).getId() + " " + user.getId());
            throw new NotFoundException("ID пользователя не соотвествует Владельцу");
        } else {
            if (itemDto.getAvailable() != null) {
                oldItem.setAvailable(itemDto.getAvailable());
            }
            if (itemDto.getDescription() != null) {
                oldItem.setDescription(itemDto.getDescription());
            }
            if (itemDto.getName() != null) {
                oldItem.setName(itemDto.getName());
            }
        }
        return ItemMapper.toItemDto(oldItem);
    }


    @Override
    public ItemDtoWithBooking getByItemId(Long itemId) {
        log.info("Получение  Item  по ID" + itemId);
        ItemDtoWithBooking itemDto = ItemMapper.itemDtoWithBooking(itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item не найден")));

        log.info("Получен Item" + itemDto);
        return itemDto;
    }

    @Override
    public ItemDtoWithBooking getByOwnerIdAndUserId(Long itemId, Long userId) {
        Item item = getItem(itemId);
        ItemDtoWithBooking itemDto = ItemMapper.itemDtoWithBooking(item);
        log.info("Установка комментариев" + itemDto);
        itemDto.setComments(CommentMapper.toCommentDtos(commentRepository.findAllByItemId(itemDto.getId())));
        log.info(String.valueOf(itemDto));
        if (!item.getOwner().getId().equals(userId)) {
            return itemDto;
        }
        log.info("Вывод item по bookerId" + itemDto);

        Booking lastBooking = bookingRepository.findFirstByItemIdAndEndIsBefore(itemDto.getId(), LocalDateTime.now(), sortByDescEnd);
        Booking nextBooking = bookingRepository.findFirstByItemIdAndStartIsAfterOrderByEnd(itemDto.getId(), LocalDateTime.now());
        if (lastBooking != null) {
            itemDto.setLastBooking(BookingMapper.bookingShortDto(lastBooking));
        }
        if (nextBooking != null) {
            itemDto.setNextBooking(BookingMapper.bookingShortDto(nextBooking));
        }
        return itemDto;
    }

    @Override
    public List<ItemDtoWithBooking> getAllByUserId(Long userId, Integer from, Integer size) {
        getUser(userId);
        List<Item> items = itemRepository.findItemsByOwnerId(userId);
        Map<Item, List<Booking>> approvedBooking = bookingRepository
                .findAllByItemIn(items, Sort.by(Sort.Direction.ASC, "end"))
                .stream()
                .collect(Collectors.groupingBy(Booking::getItem));
        System.out.println(approvedBooking);
        Map<Item, List<Comment>> comments = commentRepository
                .findAllByItemIn(items)
                .stream()
                .collect(Collectors.groupingBy(Comment::getItem));
        List<ItemDtoWithBooking> itemDtos = new ArrayList<>();
        for (Item item : items) {
            ItemDtoWithBooking itemDtoWithBooking = ItemMapper.itemDtoWithBooking(item);
            List<Booking> itemBookings = approvedBooking.get(item);
            Booking last = null;
            Booking next = null;
            System.out.println("itemBooking" + itemBookings);
            if (itemBookings != null) {
                next = itemBookings.stream().filter(i -> i.getStart().isAfter(LocalDateTime.now())).findFirst().orElse(null);
                last = itemBookings.stream().filter(i -> i.getEnd().isBefore(LocalDateTime.now())).findFirst().orElse(null);
            }

            if (last != null) {
                itemDtoWithBooking.setLastBooking(BookingMapper.bookingShortDto(last));
            }
            if (next != null) {
                itemDtoWithBooking.setNextBooking(BookingMapper.bookingShortDto(next));
            }
            List<Comment> itemComments = comments.get(item);
            if (itemComments != null) {
                System.out.println(comments.get(item));
                itemDtoWithBooking.setComments(CommentMapper.toCommentDtos(itemComments));
            }

            itemDtos.add(itemDtoWithBooking);
            System.out.println(itemDtos);
        }
        return itemDtos;



    }

    @Override
    public List<ItemDto> getByText(String text, Integer from, Integer size) {

        return itemRepository.findItemsByDescriptionContainingIgnoreCaseAndAvailableIsTrueOrNameContainingIgnoreCaseAndAvailableIsTrue(text, text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("неверный user ID"));
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("неверный Item ID"));
    }


}
