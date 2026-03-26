package ru.yandex.practicum.filmorate.model;


import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Set<Long> genreIds = new HashSet<>();
    private Long mpaId;
    private int duration;
    private Set<Long> likes = new HashSet<>();
}
