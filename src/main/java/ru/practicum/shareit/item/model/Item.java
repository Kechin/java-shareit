package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")

public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false, length = 255)
    private String name;
    @Column(nullable = false, length = 1000)
    private String description;
    @Column(name = "is_available", nullable = false)
    private Boolean available;
    @OneToOne(optional = false)
    @JoinColumn(name = "owner_id")
    private User owner;
    @OneToOne(optional = true)
    @JoinColumn(name = "request_id")
    private ItemRequest itemRequest;

    public Item(long l, String pc, String s, boolean b, User user1) {
        this.setId(l);
        this.setName(pc);
        this.setDescription(s);
        this.setAvailable(b);
        this.setOwner(user1);
    }
}
