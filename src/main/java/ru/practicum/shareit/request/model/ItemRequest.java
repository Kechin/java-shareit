package ru.practicum.shareit.request.model;


import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests")
@ToString
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false, length = 1000)
    private String description;
    @Column(nullable = false, name = "created")
    private LocalDateTime created; //created
    @ManyToOne(optional = false)
    @JoinColumn(name = "requester_id")
    private User requester;

}