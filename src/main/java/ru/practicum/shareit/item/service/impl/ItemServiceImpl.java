package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        log.info("Запрос на добавление " + userId + " " + itemDto);

        User owner = getUserById(userId);
        Item newItem = ItemMapper.toItem(itemDto, owner);
        return ItemMapper.toItemDto(itemRepository.add(newItem));
    }

    @Override
    public ItemDto update(Long id, ItemDto itemDto, Long userId) {
        log.info("Запрос на обновление " + userId + " " + itemDto);
        Item oldItem = getItemById(id);
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
        return ItemMapper.toItemDto(getItemById(id));
    }

    private Item getItemById(Long id) {
        return itemRepository.getById(id).orElseThrow(() ->
                new NotFoundException("Item с указанным ID НЕТ!"));
    }

    private User getUserById(Long id) {
        return userRepository.getUserById(id).orElseThrow(() ->
                new NotFoundException("Item с указанным ID НЕТ!"));
    }

    @Override
    public List<ItemDto> getByUserId(Long userId) {
        List<Item> items = itemRepository.getByUser(userId).orElseThrow(() ->
                new NotFoundException("Пользователя с указанным ID НЕТ!"));
        return ItemMapper.itemDtos(items);
    }

    @Override
    public List<ItemDto> getByText(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> items = itemRepository.getByDescription(text.toLowerCase());
        return ItemMapper.itemDtos(items);
    }
}
