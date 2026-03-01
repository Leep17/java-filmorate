package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;


@SpringBootTest
public class UserApplicationTests {

    private final UserController userController = new UserController();

    private User testUser() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("login");
        user.setName("Name");
        user.setBirthday(LocalDate.of(2025,1,1));
        return user;
    }

    @Test
    void contextLoads() {
        User created = userController.create(testUser());

        Assertions.assertNotNull(created.getId());
        Assertions.assertEquals("login", created.getLogin());
    }

    @Test
    void emptyName() {
        User user = testUser();
        user.setName("");

        User created = userController.create(user);

        Assertions.assertEquals(created.getLogin(), created.getName());
    }

    @Test
    void emailWrong() {
        User user = testUser();
        user.setEmail("wrongEmail");

        Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void loginSpace() {
        User user = testUser();
        user.setLogin("log in");

        Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void birthdayInFuture() {
        User user = testUser();
        user.setBirthday(LocalDate.now().plusDays(1));

        Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
    }
}
