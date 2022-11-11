package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;


@Getter
@Setter

@NoArgsConstructor
@Entity
@Data
@Table(name = "users")
@ToString

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;
    @Column(nullable = false, length = 256)
    private String name;
    @Column(name = "email", unique = true, nullable = false, length = 512)
    private String email;

    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
