package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserDbStorage implements UserStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String UPDATE_QUERY = "UPDATE users SET name = ?, email = ?, login = ?, birthday = ? WHERE id = ?";
    private static final String ADD_FRIEND_QUERY = "INSERT INTO friendship (user_id, friend_id, status) VALUES (?, ?, ?)";
    private static final String DELETE_FRIEND_QUERY = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
    private static final String GET_FRIENDS_QUERY =
            "SELECT u.* " +
                    "FROM users u " +
                    "JOIN friendship f ON u.id = f.friend_id " +
                    "WHERE f.user_id = ? " +
                    "ORDER BY u.id";
    private static final String GET_COMMON_FRIENDS_QUERY =
            "SELECT u.* " +
                    "FROM users u " +
                    "JOIN friendship f1 ON u.id = f1.friend_id " +
                    "JOIN friendship f2 ON u.id = f2.friend_id " +
                    "WHERE f1.user_id = ? AND f2.user_id = ? " +
                    "ORDER BY u.id";

    private final JdbcTemplate jdbc;
    private final RowMapper<User> userRowMapper;

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> userRowMapper) {
        this.jdbc = jdbc;
        this.userRowMapper = userRowMapper;
    }
    @Override
    public User create(User user) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbc)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        long id = insert.executeAndReturnKey(
                Map.of(
                        "email", user.getEmail(),
                        "login", user.getLogin(),
                        "name", user.getName(),
                        "birthday", user.getBirthday()
                )
        ).longValue();
        user.setId(id);
        return user;
    }

    @Override
    public User update(User user) {
        int rowsUpdated = jdbc.update(UPDATE_QUERY,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getId());

        if (rowsUpdated == 0) {
            throw new NotFoundException("Пользователь с id=" + user.getId() + " не найден");
        }
        return user;
    }

    @Override
    public Collection<User> findAll() {
        return jdbc.query(FIND_ALL_QUERY, userRowMapper);
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return jdbc.query(FIND_BY_ID_QUERY, userRowMapper, id)
                .stream()
                .findFirst();
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        jdbc.update(ADD_FRIEND_QUERY, userId, friendId, "UNCONFIRMED");
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        jdbc.update(DELETE_FRIEND_QUERY, userId, friendId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        return jdbc.query(GET_FRIENDS_QUERY, userRowMapper, userId);
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) {
        return jdbc.query(GET_COMMON_FRIENDS_QUERY, userRowMapper, userId, otherId);
    }
}
