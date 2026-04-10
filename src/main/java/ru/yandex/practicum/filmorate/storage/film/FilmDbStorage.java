package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Repository
public class FilmDbStorage implements FilmStorage {

    private static final String FIND_ALL_QUERY =
            "SELECT f.*, m.name AS mpa_name " + "FROM films f " + "LEFT JOIN mpa m ON f.mpa_id = m.id " + "ORDER BY f.id";
    private static final String FIND_BY_ID_QUERY =
            "SELECT f.*, m.name AS mpa_name " + "FROM films f " + "LEFT JOIN mpa m ON f.mpa_id = m.id " + "WHERE f.id = ?";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
    private static final String DELETE_FILM_GENRES = "DELETE FROM film_genre WHERE film_id = ?";
    private static final String INSERT_FILM_GENRE = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
    private static final String ADD_LIKE_QUERY = "MERGE INTO film_likes (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
    private static final String GET_TOP_FILMS_QUERY =
            "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name AS mpa_name " +
                    "FROM films f " +
                    "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                    "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
                    "GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name " +
                    "ORDER BY COUNT(fl.user_id) DESC, f.id ASC " +
                    "LIMIT ?";
    private static final String FIND_GENRES_BY_FILM_ID =
            "SELECT g.id, g.name_genre " + "FROM film_genre fg " + "JOIN genres g ON fg.genre_id = g.id " + "WHERE fg.film_id = ? " + "ORDER BY g.id";

    private final JdbcTemplate jdbc;
    private final RowMapper<Film> filmRowMapper;
    private final RowMapper<Genre> genreRowMapper;

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> filmRowMapper, RowMapper<Genre> genreRowMapper) {
        this.jdbc = jdbc;
        this.filmRowMapper = filmRowMapper;
        this.genreRowMapper = genreRowMapper;
    }

    @Override
    public Collection<Film> findAll() {
        return jdbc.query(FIND_ALL_QUERY, filmRowMapper)
                .stream()
                .map(film -> loadFilmDetails(film))
                .toList();
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbc)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        long id = insert.executeAndReturnKey(
                Map.of(
                        "name", film.getName(),
                        "description", film.getDescription(),
                        "release_date", film.getReleaseDate(),
                        "duration", film.getDuration(),
                        "mpa_id", film.getMpa().getId()
                )
        ).longValue();
        film.setId(id);
        saveGenres(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        int rowsUpdated = jdbc.update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        if (rowsUpdated == 0) {
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден");
        }

        jdbc.update(DELETE_FILM_GENRES, film.getId());
        saveGenres(film);
        return film;
    }

    private void saveGenres(Film film) {
        if (film.getGenres() == null) {
            return;
        }
        for (Genre genre : film.getGenres()) {
            jdbc.update(INSERT_FILM_GENRE, film.getId(), genre.getId());
        }
    }

    private Film loadFilmDetails(Film film) {
        List<Genre> genres = jdbc.query(FIND_GENRES_BY_FILM_ID, genreRowMapper, film.getId());
        film.setGenres(new HashSet<>(genres));
        return film;
    }

    @Override
    public Optional<Film> findFilmById(Long id) {
        return jdbc.query(FIND_BY_ID_QUERY, filmRowMapper, id)
                .stream()
                .findFirst()
                .map(film -> loadFilmDetails(film));
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        jdbc.update(ADD_LIKE_QUERY, filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        jdbc.update(DELETE_LIKE_QUERY, filmId, userId);
    }

    @Override
    public List<Film> getTopFilms(int count) {
        return jdbc.query(GET_TOP_FILMS_QUERY, filmRowMapper, count)
                .stream()
                .map(film -> loadFilmDetails(film))
                .toList();
    }
}
