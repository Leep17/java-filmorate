MERGE INTO mpa (id, name) KEY (id) VALUES (1, 'G');
MERGE INTO mpa (id, name) KEY (id) VALUES (2, 'PG');
MERGE INTO mpa (id, name) KEY (id) VALUES (3, 'PG-13');
MERGE INTO mpa (id, name) KEY (id) VALUES (4, 'R');
MERGE INTO mpa (id, name) KEY (id) VALUES (5, 'NC-17');

MERGE INTO genres (id, name_genre) KEY (id) VALUES (1, 'Комедия');
MERGE INTO genres (id, name_genre) KEY (id) VALUES (2, 'Драма');
MERGE INTO genres (id, name_genre) KEY (id) VALUES (3, 'Мультфильм');
MERGE INTO genres (id, name_genre) KEY (id) VALUES (4, 'Триллер');
MERGE INTO genres (id, name_genre) KEY (id) VALUES (5, 'Документальный');
MERGE INTO genres (id, name_genre) KEY (id) VALUES (6, 'Боевик');

MERGE INTO users (id, email, login, name, birthday) KEY (id)
VALUES (1, 'test@mail.ru', 'testLogin', 'Test', DATE '2000-01-01');

MERGE INTO films (id, name, description, release_date, duration, mpa_id) KEY (id)
VALUES (1, 'Test Film', 'Test description', DATE '2000-01-01', 120, 1);