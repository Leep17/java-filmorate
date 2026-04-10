package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Repository
public class GenreDbStorage implements GenreStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM genres";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE id = ?";

    private final JdbcTemplate jdbc;
    private final RowMapper<Genre> genreRowMapper;

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> genreRowMapper) {
        this.jdbc = jdbc;
        this.genreRowMapper = genreRowMapper;
    }

    @Override
    public Collection<Genre> findAll() {
        return jdbc.query(FIND_ALL_QUERY, genreRowMapper);
    }

    @Override
    public Genre findGenreById(Long id) {
        try {
        return jdbc.queryForObject(FIND_BY_ID_QUERY, genreRowMapper, id);
        } catch (EmptyResultDataAccessException ignored) {
               throw new NotFoundException("Жанр с id=" + id + " не найден");
             }
    }
}
