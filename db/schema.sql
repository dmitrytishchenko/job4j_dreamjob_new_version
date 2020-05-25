CREATE table if not exists post (
id serial primary key,
name text,
description text,
date text
);
CREATE table if not exists candidate (
id serial primary key,
name text,
photoId text
);
create table if not exists users (
id serial primary key,
name text unique,
email text,
password text
);