package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;


@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findItemsByDescriptionContainingIgnoreCaseAndAvailableIsTrueOrNameContainingIgnoreCaseAndAvailableIsTrue(String text1, String text2);

    List<Item> findItemsByOwnerIdOrderById(Long id);

    List<Item> findItemsByItemRequest_Id(Long id);

    List<Item> findByIdIn(List ids);

    List<Item> findAllByItemRequestIn(List<ItemRequest> itemRequests);

}

