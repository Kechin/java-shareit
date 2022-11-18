package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments")
@ToString
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false, length = 1000)
    private String text;

    @ManyToOne(optional = false)
    @JoinColumn(name = "item_id")
    private Item item;
    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id")
    private User author;
    @Column(nullable = false)
    private LocalDateTime created;
}
