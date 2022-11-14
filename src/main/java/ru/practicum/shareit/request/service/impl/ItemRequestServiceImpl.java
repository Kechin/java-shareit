package ru.practicum.shareit.request.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;


    @Transactional
    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId) {
        itemRequestDto.setCreated(LocalDateTime.now());
        log.info("Попытка создать новый реквест" + getUser(userId));
        ItemRequest itemRequest = new ItemRequest(null, itemRequestDto.getDescription(), itemRequestDto.getCreated(), getUser(userId));
        return ItemRequestMapper.itemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public ItemRequestDto getByRequestId(Long requestId, Long requesterId) {
        getUser(requesterId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(" Неверный Itemrequest ID"));
        return setItems(ItemRequestMapper.itemRequestDto(itemRequest));

    }

    @Override
    public List<ItemRequestDto> getAll(Long userId, Integer from, Integer size) {

        Pageable pageable = PageRequest.of(from, size);

        Page<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdIsNotLike(userId, pageable);
        List<ItemRequestDto> itemRequestDtos = ItemRequestMapper.itemRequestDtos(requests.toList());

        List<Item> items = itemRepository.findAllByItemRequestIn(requests.toList());
        for (ItemRequestDto itemReg : itemRequestDtos) {
            List<Item> items1 = items.stream()
                    .filter(i -> Objects.equals(i.getItemRequest().getId(), itemReg.getId()))
                    .collect(Collectors.toList());
            if (items1 == null) {
                items1 = Collections.emptyList();
            }
            itemReg.setItems(ItemMapper.itemDtoWithBookings(items1));
        }
        return itemRequestDtos;

    }

    @Override
    public List<ItemRequestDto> getAllForRequester(Long requesterId) {
        getUser(requesterId);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterId(requesterId);
        List<ItemRequestDto> itemRequestDtos = ItemRequestMapper.itemRequestDtos(requests);

        List<Item> items = itemRepository.findAllByItemRequestIn(requests);
        for (ItemRequestDto itemReg : itemRequestDtos) {
            List<Item> items1 = items.stream()
                    .filter(i -> Objects.equals(i.getItemRequest().getId(), itemReg.getId()))
                    .collect(Collectors.toList());
            if (items1 == null) {
                items1 = Collections.emptyList();
            }
            itemReg.setItems(ItemMapper.itemDtoWithBookings(items1));
        }
        return itemRequestDtos;
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("неверный User ID"));
    }


    private ItemRequestDto setItems(ItemRequestDto itemrequestDto) {
        List<Item> items = itemRepository.findItemsByItemRequest_Id(itemrequestDto.getId());
        if (items == null) {
            itemrequestDto.setItems(Collections.emptyList());
            return itemrequestDto;
        }
        itemrequestDto.setItems(ItemMapper.itemDtoWithBookings(items));
        return itemrequestDto;
    }
}
