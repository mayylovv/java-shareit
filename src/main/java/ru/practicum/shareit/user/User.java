package ru.practicum.shareit.user;

import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    long id;
    String name;
    String email;
}
