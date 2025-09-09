insert into authors(full_name)
values ('Author_1'),
       ('Author_2'),
       ('Author_3');

insert into genres(name)
values ('Genre_1'),
       ('Genre_2'),
       ('Genre_3'),
       ('Genre_4'),
       ('Genre_5'),
       ('Genre_6');

insert into books(title, author_id)
values ('BookTitle_1', 1),
       ('BookTitle_2', 2),
       ('BookTitle_3', 3);

insert into books_genres(book_id, genre_id)
values (1, 1),
       (1, 2),
       (2, 3),
       (2, 4),
       (3, 5),
       (3, 6);

insert into commentaries(book_id, text)
values (1, 'comment11'),
       (1, 'comment12'),
       (2, 'comment21'),
       (3, 'comment31'),
       (3, 'comment32'),
       (3, 'comment33');

insert into users(username, password)
values ('admin', '$2a$12$t3r/4wTmYU4/SnUcCHGZaOSBmUhTc3mJFoT3g6oEXdecA5aGPsxV.'),-- ''admin'),
       ('user', '$2a$12$ompACDrhhPifepnzeFc9Ges4NOTYs2YLE607.aVi3SiTYl7tslQx2');--'user');

insert into USER_ROLES(USER_ID, ROLE)
values (1, 'ADMIN'),
       (1, 'USER'),
       (2, 'USER');