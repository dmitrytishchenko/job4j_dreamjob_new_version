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