package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    @Override
    public ItemDto create(ItemDto itemDto, Long userId)  {
        log.info("Запрос на добавление " + userId + " " + itemDto);
        itemDto.setOwner(UserMapper.toUserDto(getUser(userId)));
        Item newItem = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto( itemRepository.save(newItem));
    }
    @Override
    public ItemDto update(Long id, ItemDto itemDto, Long userId) {
        log.info("Запрос на обновление " + userId + " " + itemDto);
        User user = getUser(userId);
        Item oldItem= getItem(id);
        if (!userId.equals( oldItem.getOwner().getId())) {
            System.out.println("id old"+ getItem(id).getId()+" "+user.getId());
            throw new NotFoundException("ID пользователя не соотвествует Владельцу");
        }
        else {
            if (itemDto.getAvailable()!=null){
                oldItem.setAvailable(itemDto.getAvailable());
            }
            if (itemDto.getDescription()!=null){
                oldItem.setDescription(itemDto.getDescription());
            }
            if(itemDto.getName()!=null){
                oldItem.setName(itemDto.getName());
            }
            itemRepository.save(oldItem);
        }
        return ItemMapper.toItemDto(oldItem);
    }


   @Override
    public ItemDtoWithBooking getByItemId(Long itemId){
        log.info("Получение  Item  по ID"+itemId);
       ItemDtoWithBooking itemDto =  ItemMapper.itemDtoWithBooking(itemRepository.findById(itemId).orElseThrow(()->
       new NotFoundException("Item не найден")));

       log.info("Получен Item"+itemDto);
       return itemDto;
    }

    @Override
    public ItemDtoWithBooking getByOwnerIdAndUserId(Long itemId, Long userId)  {
        Item item =getItem(itemId);

        if(!item.getOwner().getId().equals(userId)){
            return ItemMapper.itemDtoWithBooking(item);
        }
        log.info("Вывод item по bookerId");

       return setComments( ItemMapper.itemDtoWithBooking(item));

    }
    @Override
    public List<ItemDto> getAllByUserId(Long userId){
        getUser(userId);
        List< ItemDto> itemDto =ItemMapper.toItemDtos( itemRepository.findItemsByOwnerId(userId));
        log.info("getAllByUserId"+itemDto);
        return itemDto;
    }
    @Override
    public List<ItemDto> getByText(String text) {
        if (text.isBlank()) {return Collections.emptyList();}
        return itemRepository
               .findItemsByDescriptionContainingIgnoreCaseAndAvailableIsTrueOrNameContainingIgnoreCaseAndAvailableIsTrue //lol
                       (text,text).stream().map(ItemMapper::toItemDto)
               .collect(Collectors.toList());
    }


    private User getUser(Long userId)  {

        return  userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("неверный ID"));
    }
    private ItemDtoWithBooking setComments(ItemDtoWithBooking itemDto){
       log.info(commentRepository.findAll().toString());
        itemDto.setComments(CommentMapper.toCommentDtos( commentRepository.findCommentsByItem_Id(itemDto.getId())));
        Booking lastBooking= bookingRepository
                .findFirstByItemIdAndEndIsBeforeOrderByEndDesc(itemDto.getId(), LocalDateTime.now());
        Booking nextBooking= bookingRepository
                .findFirstByItemIdAndStartIsAfterOrderByEnd(itemDto.getId(),LocalDateTime.now());
                    if (lastBooking!=null){itemDto.setLastBooking(BookingMapper.bookingShortDto(lastBooking));}
                    if (nextBooking!=null){itemDto.setNextBooking(BookingMapper.bookingShortDto(nextBooking));}


        return itemDto;
    }
    private ItemDtoWithBooking setComments2(ItemDtoWithBooking itemDto, Long bookerId){
        log.info("Установка комментариев" + itemDto);
        itemDto.setComments(CommentMapper.toCommentDtos( commentRepository.findCommentsByItem_Id(itemDto.getId())));
        Booking lastBooking= bookingRepository
                .findFirstByItem_IdAndAndBooker_IdAndEndBeforeOrderByEndDesc(itemDto.getId(),bookerId, LocalDateTime.now());
        Booking nextBooking= bookingRepository
                .findFirstByItem_IdAndAndBooker_IdAndStartIsAfterOrderByEnd(itemDto.getId(),bookerId,LocalDateTime.now());

            itemDto.setLastBooking(BookingMapper.bookingShortDto(lastBooking));

            itemDto.setNextBooking(BookingMapper.bookingShortDto(nextBooking));


        return itemDto;
    }
    private Item getItem(Long id) {
        return ( (itemRepository.findById(id)).orElseThrow(()->
                new NotFoundException("Item c данным Id не найден")));
    }
}
