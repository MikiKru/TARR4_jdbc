CREATE DATABASE task_manager;
USE task_manager;
# users
CREATE TABLE tm_user (
	user_id int primary key auto_increment,
    user_name varchar(255) not null,
    user_lastname varchar(255) not null,
    user_email varchar(255) unique not null,
    user_password varchar (255) not null,
    user_registration datetime default now(),
    user_status bool default 1
);
# roles
CREATE TABLE tm_role(
	role_id int primary key auto_increment,
    role_name varchar(255) not null
);
# RELACJA ManyToMany USER i ROLE
CREATE TABLE user_role(
	user_id int,
    role_id int,
    FOREIGN KEY (user_id) REFERENCES tm_user (user_id),
    FOREIGN KEY (role_id) REFERENCES tm_role (role_id)
);
insert into tm_user values (default, 'Michał', 'Kruczkowski', 'mk@mk.pl', 'mk', default, default);
insert into tm_user values (default, 'Adam', 'Kowalski', 'ak@ak.pl', 'ak', default, default);
insert into tm_role values (default, 'admin');
insert into tm_role values (default, 'user');
insert into user_role values (1, 1);
insert into user_role values (1, 2);
insert into user_role values (2, 2);

select * from tm_user;
select * from tm_role;
select * from user_role;

select last_insert_id();

delete from tm_user where user_id = 1;

# Utworzenie nowego użytkownika na serwerze DB i przypisanie mu uprawnień
CREATE USER 'tm_user'@'localhost' IDENTIFIED BY 'qwe123';
GRANT SELECT, INSERT, UPDATE, DELETE ON task_manager.* TO 'tm_user'@'localhost';

# email, registration, status, role_name
SELECT u.user_email, u.user_registration, u.user_status, r.role_name FROM tm_user u join  user_role ur on (u.user_id = ur.user_id) join tm_role r on (r.role_id = ur.role_id) order by u.user_registration;
delete from user_role where user_id = 1 and role_id = 1;


select * from user_role where role_id = (select role_id from tm_role where role_name = 'admin') and user_id = 1;

insert into user_role values (1, (select role_id from tm_role where role_name = 'admin'))




