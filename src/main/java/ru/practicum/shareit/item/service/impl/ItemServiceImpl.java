package ru.practicum.shareit.item.service.impl;

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
import java.util.List;
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
        setComments(itemDto);
        if (!item.getOwner().getId().equals(userId)) {
            return itemDto;
        }
        log.info("Вывод item по bookerId" + itemDto);
        return setLastAndNextBookings(itemDto);
    }

    @Override
    public List<ItemDtoWithBooking> getAllByUserId(Long userId, Integer from, Integer size) {
        getUser(userId);
        List<ItemDtoWithBooking> itemDto = ItemMapper.itemDtoWithBookings(itemRepository.findItemsByOwnerId(userId));
        return itemDto.stream().map(i -> setLastAndNextBookings(i)).map(i -> setComments(i)).collect(Collectors.toList());
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

    private ItemDtoWithBooking setLastAndNextBookings(ItemDtoWithBooking itemDto) {

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

    private ItemDtoWithBooking setComments(ItemDtoWithBooking itemDto) {
        log.info("Установка комментариев" + itemDto);
        itemDto.setComments(CommentMapper.toCommentDtos(commentRepository.findAllByItemId(itemDto.getId())));
        log.info(String.valueOf(itemDto));
        return itemDto;
    }




}
