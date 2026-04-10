package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;

@Repository
public class MPADbStorage implements MPAStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM mpa";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM mpa WHERE id = ?";

    private final JdbcTemplate jdbc;
    private final RowMapper<MPA> mpaRowMapper;

    public MPADbStorage(JdbcTemplate jdbc, RowMapper<MPA> mpaRowMapper) {
        this.jdbc = jdbc;
        this.mpaRowMapper = mpaRowMapper;
    }

    @Override
    public Collection<MPA> findAll() {
        return jdbc.query(FIND_ALL_QUERY, mpaRowMapper);
    }

    @Override
    public MPA findMPAById(Long id) {
        try {
            return jdbc.queryForObject(FIND_BY_ID_QUERY, mpaRowMapper, id);
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("Рейтинг с id=" + id + " не найден");
        }
    }
}
