create table if not exists cities (
id serial primary key,
name text unique
);
CREATE table if not exists post (
id serial primary key,
name text,
description text,
date text
);
CREATE table if not exists candidate (
id serial primary key,
name text,
city_id int,
photoId text,
foreign key (city_id) references cities(id)
);
create table if not exists users (
id serial primary key,
name text unique,
email text,
password text
);
