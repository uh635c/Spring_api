--liquibase formatted sql
--changeset uh635c:step1
create table if not exists users (
    id int not null auto_increment,
    email varchar(100) not null unique,
    first_name varchar(50) not null,
    last_name varchar(50) not null,
    password varchar(100) not null,
    role varchar(20) not null default 'USER',
    status varchar(20) not null default 'ACTIVE',
    primary key(id)
);

--changeset uh635c:step2
insert into users(email, first_name, last_name, password, role, status) values('first@mail.ru', 'firstName', 'lastName', '$2a$12$B2dWD1i.heGaufxQCHzaYORgzoDU7cg3fU9oTtzfzzT1iXIK44gc.', 'ROLE_USER', 'ACTIVE');
insert into users(email, first_name, last_name, password, role, status) values('second@mail.ru', 'secondName', 'lastName', '$2a$12$YCcfhdiToajv70NnSx3y0OLTHJG2ELfTZYbcgECzpTKntyZhI0r.K', 'ROLE_USER', 'ACTIVE');
insert into users(email, first_name, last_name, password, role, status) values('third@mail.ru', 'thirdName', 'lastName', '$2a$12$vXEzjzrtS8sHfhvS8Xzll.pN1cmOACSWAW4X9eNialgypYSRswkDm', 'ROLE_MODERATOR', 'ACTIVE');
insert into users(email, first_name, last_name, password, role, status) values('forth@mail.ru', 'forthName', 'lastName', '$2a$12$dzFuKkJGm1YrFWi/EyMe..E0Ewp7qeqKGviSgzFuwnsEzj7/1T/m.', 'ROLE_MODERATOR', 'ACTIVE');
insert into users(email, first_name, last_name, password, role, status) values('fifth@mail.ru', 'fifthName', 'lastName', '$2a$12$PH9Evo9ySAZkn/AyN6fJ7OrnjVgGP4itveTUU0qBYFUnRGJj.yI16', 'ROLE_ADMIN', 'ACTIVE');

--changeset uh635c:step3
create table if not exists files(
    id int not null auto_increment,
    location varchar(250) not null,
    size int not null,
    user_id int not null,
    status varchar(20) not null,
    constraint file_user foreign key(user_id) references users(id),
    primary key(id)
);

--changeset uh635c:step4
insert into files(location, size, user_id, status) values('location1', 100, 1, 'ACTIVE');
insert into files(location, size, user_id, status) values('location2', 200, 2, 'ACTIVE');
insert into files(location, size, user_id, status) values('location3', 300, 3, 'ACTIVE');
insert into files(location, size, user_id, status) values('location4', 400, 4, 'ACTIVE');
insert into files(location, size, user_id, status) values('location5', 500, 5, 'ACTIVE');
insert into files(location, size, user_id, status) values('location6', 600, 1, 'ACTIVE');
insert into files(location, size, user_id, status) values('location7', 700, 1, 'INACTIVE');
insert into files(location, size, user_id, status) values('location8', 800, 2, 'INACTIVE');
insert into files(location, size, user_id, status) values('location9', 900, 3, 'INACTIVE');

--changeset uh635c:step5
create table if not exists events(
    id int not null auto_increment,
    date date not null,
    description varchar(20) not null,
    user_id int not null,
    file_id int not null,
    constraint event_user foreign key(user_id) references users(id),
    constraint event_file foreign key(file_id) references files(id),
    primary key(id)
);

--changeset uh635c:step6
insert into events(date, description, user_id, file_id) values("2022-06-01", " FILE_CREATED", 1, 1);
insert into events(date, description, user_id, file_id) values("2022-06-01", " FILE_CREATED", 2, 2);
insert into events(date, description, user_id, file_id) values("2022-06-01", " FILE_CREATED", 3, 3);
insert into events(date, description, user_id, file_id) values("2022-06-01", " FILE_CREATED", 4, 4);
insert into events(date, description, user_id, file_id) values("2022-06-01", " FILE_CREATED", 5, 5);
insert into events(date, description, user_id, file_id) values("2022-06-01", " FILE_CREATED", 1, 6);
insert into events(date, description, user_id, file_id) values("2022-06-01", " FILE_CREATED", 1, 7);
insert into events(date, description, user_id, file_id) values("2022-06-01", " FILE_CREATED", 2, 8);
insert into events(date, description, user_id, file_id) values("2022-06-01", " FILE_CREATED", 3, 9);
insert into events(date, description, user_id, file_id) values("2022-06-01", " FILE_DELETED", 1, 7);
insert into events(date, description, user_id, file_id) values("2022-06-01", " FILE_DELETED", 2, 8);
insert into events(date, description, user_id, file_id) values("2022-06-01", " FILE_DELETED", 3, 9);


