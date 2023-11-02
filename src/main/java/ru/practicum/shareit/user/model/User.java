package ru.practicum.shareit.user.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@Data
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    long id;
    @NotBlank
    String name;
    @Email
    @NonNull
    String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
