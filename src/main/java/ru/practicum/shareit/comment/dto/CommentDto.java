package ru.practicum.shareit.comment.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CommentDto {

    Long id;
    String text;
    String authorName;
    LocalDateTime created;
}