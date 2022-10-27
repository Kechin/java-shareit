package ru.practicum.shareit.user.model;

import lombok.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@ToString
@EqualsAndHashCode
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;
    @Column
    @NotNull(groups = Create.class)
    private String name;
    @Column(name = "email", unique = true)
    @Email(groups = {Create.class, Update.class}, message = "Неверный e-mail")
    @NotNull(groups = Create.class)
    private String email;

}
