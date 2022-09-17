package ru.practicum.shareit.item.service.impl;

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
import java.util.List;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        log.info("Запрос на добавление " + userId + " " + itemDto);
        Item newItem = ItemMapper.toItem(itemDto, userRepository.getUser(userId));
        return ItemMapper.toItemDto(itemRepository.add(newItem));
    }

    @Override
    public ItemDto update(Long id, ItemDto itemDto, Long userId) {
        log.info("Запрос на обновление " + userId + " " + itemDto);

        Item updatetItem = ItemMapper.toItem(itemDto, userRepository.getUser(userId));
        Item oldItem = itemRepository.getById(id);
        if (!userId.equals(oldItem.getOwner().getId())) {
            throw new NotFoundException("ID пользователя не соотвествует Владельцу");
        }
        updatetItem.setId(id);
        if (updatetItem.getName() == null) {
            updatetItem.setName(oldItem.getName());
        }
        if (updatetItem.getDescription() == null) {
            updatetItem.setDescription(oldItem.getDescription());
        }
        if (updatetItem.getAvailable() == null) {
            updatetItem.setAvailable(oldItem.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.update(updatetItem));
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

        List<ItemDto> itemDtos = new ArrayList<>();
        if (text.equals("")) {
            return itemDtos;
        }
        List<Item> items = itemRepository.getByDescription(text.toLowerCase());

        for (Item item : items) {
            itemDtos.add(ItemMapper.toItemDto(item));
        }
        return itemDtos;
    }
}
