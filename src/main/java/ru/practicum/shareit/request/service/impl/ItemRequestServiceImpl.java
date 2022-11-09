package ru.practicum.shareit.request.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

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

        return setItems(ItemRequestMapper.itemRequestDto(getItemRequest(requestId)));

    }

    @Override
    public List<ItemRequestDto> getAll(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        Page<ItemRequest> itemRequestsPage = itemRequestRepository.findAllByRequesterIdIsNotLike(userId, pageable);
        List<ItemRequestDto> itemRequestDtos = ItemRequestMapper.itemRequestDtos(itemRequestsPage.toList());
        return itemRequestDtos.stream().map(this::setItems).collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllForRequester(Long requesterId) {
        getUser(requesterId);
        log.info("Попытка получить все реквесты пользователя.");
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterId(requesterId);
        if (requests == null) {
            return Collections.emptyList();
        }
        List<ItemRequestDto> itemRequestDto = ItemRequestMapper
                .itemRequestDtos(itemRequestRepository
                        .findAllByRequesterId(requesterId))
                .stream().map(this::setItems).collect(Collectors.toList());

        return itemRequestDto;
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("неверный User ID"));
    }

    private ItemRequest getItemRequest(Long itemReq) {
        return itemRequestRepository.findById(itemReq).orElseThrow(() ->
                new NotFoundException(" Неверный Itemrequest ID"));
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
