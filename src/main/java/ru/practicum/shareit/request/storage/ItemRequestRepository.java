package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByIdBetween(Long first, Long last);

    List<ItemRequest> findAllByRequesterId(Long requesterId);

    ItemRequest findItemRequestByIdAndRequester_Id(Long requestId, Long requesterId);

    Page<ItemRequest> findAllByRequesterIdIsNotLike(Long id, Pageable pageable);

}

