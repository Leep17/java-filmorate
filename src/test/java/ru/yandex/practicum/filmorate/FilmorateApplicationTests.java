package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@SpringBootTest
class FilmorateApplicationTests {
    private final FilmController filmController = new FilmController();
    private static final LocalDate minDate = LocalDate.of(1895, 12, 28);

    private Film testFilm() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(minDate);
        film.setDuration(100);
        return film;
    }

	@Test
	void contextLoads() {
        Film created = filmController.create(testFilm());

        Assertions.assertNotNull(created.getId());
        Assertions.assertEquals("Film", created.getName());
	}


    @Test
    void description200() {
        Film film = testFilm();
        film.setDescription("A".repeat(200));

        Film created = filmController.create(film);

        Assertions.assertEquals(200, created.getDescription().length());
    }

    @Test
    void emptyName() {
        Film film = testFilm();
        film.setName("");

        Assertions.assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void descriptionMoreThan200() {
        Film film = testFilm();
        film.setDescription("A".repeat(201));

        Assertions.assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void releaseDateBefore1895() {
        Film film = testFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        Assertions.assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void durationZero() {
        Film film = testFilm();
        film.setDuration(0);

        Assertions.assertThrows(ValidationException.class, () -> filmController.create(film));
    }



}
