package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByIdBetween(Long first, Long last);


    ItemRequest findItemRequestByIdAndRequester_Id(Long requestId, Long requesterId);

    Page<ItemRequest> findAllByRequesterIdNot(Long id, Pageable pageRequest);

    @Modifying
    @Query("select ir from ItemRequest as ir " +
            "where ir.id = ?1 " +
            "order by ir.created desc ")
    List<ItemRequest> findAllByUserId(Long id);
}

