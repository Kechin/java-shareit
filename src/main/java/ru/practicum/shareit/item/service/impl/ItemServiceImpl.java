package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    @Autowired
    private final ItemRepository itemRepository;
    @Autowired
    private final UserRepository userRepository;

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        log.info("Запрос на добавление " + userId + " " + itemDto);
        Item newItem = ItemMapper.toItem(itemDto, userRepository.getUserById(userId));
        return ItemMapper.toItemDto(itemRepository.add(newItem));
    }

    @Override
    public ItemDto update(Long id, ItemDto itemDto, Long userId) {
        log.info("Запрос на обновление " + userId + " " + itemDto);
        Item oldItem = itemRepository.getById(id);
        if (!userId.equals(oldItem.getOwner().getId())) {
            throw new NotFoundException("ID пользователя не соотвествует Владельцу");
        }
        if (itemDto.getName() != null) {
            oldItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            oldItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            oldItem.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(oldItem);
    }

    @Override
    public ItemDto getById(Long id) {
        return ItemMapper.toItemDto(itemRepository.getById(id));
    }

    @Override
    public List<ItemDto> getByUserId(Long userId) {
        List<Item> items = itemRepository.getByUser(userId);
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            itemDtos.add(ItemMapper.toItemDto(item));
        }
        return itemDtos;
    }

    @Override
    public List<ItemDto> getByText(String text) {

        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<ItemDto> itemDtos = new ArrayList<>();
        List<Item> items = itemRepository.getByDescription(text.toLowerCase());

        for (Item item : items) {
            itemDtos.add(ItemMapper.toItemDto(item));
        }
        return itemDtos;
    }
}
