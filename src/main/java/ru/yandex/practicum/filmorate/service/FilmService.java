package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MPAStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MPAStorage mpaStorage;
    private static final LocalDate releaseDate = LocalDate.of(1895, 12, 28);


    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage, GenreStorage genreStorage, MPAStorage mpaStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    public Film findFilmById(Long id) {
        return filmStorage.findFilmById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        validationFilm(film);
        validationRateAndGenre(film);
        return filmStorage.create(film);
    }

    public Film update(Film newFilm) {
        validationFilm(newFilm);
        validationRateAndGenre(newFilm);
        return filmStorage.update(newFilm);
    }

    public void addLikes(Long filmId, Long userId) {
        findFilmById(filmId);
        userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLikes(Long filmId, Long userId) {
        findFilmById(filmId);
        userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getTopFilms(Integer count) {
        return filmStorage.getTopFilms(count);
    }

    private void validationFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }

        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом.");
        }

        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(releaseDate)) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
    }

    private void validationRateAndGenre(Film film) {
        if (film.getMpa() == null || film.getMpa().getId() == null) {
            throw new NotFoundException("Рейтинг не найден");
        }
        mpaStorage.findMPAById(film.getMpa().getId());

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                if (genre == null || genre.getId() == null) {
                    throw new NotFoundException("Жанр не найден");
                }
                genreStorage.findGenreById(genre.getId());
            }
        }
    }

}
