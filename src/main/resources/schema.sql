CREATE TABLE if not exists film
(
    "film_id"       INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "name"          varchar,
    "description"   varchar,
    "releaseDate"   date,
    "duration"      integer,
    "mpa_rating_id" integer
);

CREATE TABLE if not exists mpa_rating
(
    "rating_id" INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "name"      varchar
);

CREATE TABLE if not exists film_likes
(
    "user_id" integer,
    "film_id" integer,
    PRIMARY KEY ("user_id", "film_id")
);

CREATE TABLE if not exists genre
(
    "genre_id" INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "name"     varchar
);

CREATE TABLE if not exists film_genre
(
    "film_id"  integer PRIMARY KEY,
    "genre_id" integer
);

CREATE TABLE if not exists users
(
    "user_id"  INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    "email"    varchar,
    "login"    varchar,
    "name"     varchar,
    "birthday" date
);

CREATE TABLE if not exists friendship
(
    "user_id"   integer,
    "friend_id" integer,
    "status"    boolean
);

ALTER TABLE friendship
    ADD FOREIGN KEY ("user_id") REFERENCES users ("user_id");

ALTER TABLE film
    ADD FOREIGN KEY ("mpa_rating_id") REFERENCES mpa_rating ("rating_id");

ALTER TABLE film_likes
    ADD FOREIGN KEY ("film_id") REFERENCES film ("film_id");

ALTER TABLE film_likes
    ADD FOREIGN KEY ("user_id") REFERENCES users ("user_id");

ALTER TABLE film_genre
    ADD FOREIGN KEY ("genre_id") REFERENCES genre ("genre_id");

ALTER TABLE film_genre
    ADD FOREIGN KEY ("film_id") REFERENCES film ("film_id");

-- ALTER TABLE film


ALTER TABLE friendship
    ADD FOREIGN KEY ("friend_id") REFERENCES users ("user_id");