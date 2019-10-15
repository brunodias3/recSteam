CREATE DATABASE steam_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE steam_db

CREATE TABLE User (
    id bigint,
    display_name varchar(100),
    PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Game (
    id int,
    name varchar(100),
    description text,
    categories text,
    developers text,
    genres text,
    PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Owned_Games (
    id_user bigint,
    id_game int,
    playtime_2weeks int,
    playtime_forever int,
    PRIMARY KEY (id_user, id_game),
    FOREIGN KEY (id_user) REFERENCES User(id),
    FOREIGN KEY (id_game) REFERENCES Game(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Recommendation (
    id_user bigint,
    id_game int,
    evaluation int,
    PRIMARY KEY (id_user, id_game),
    FOREIGN KEY (id_user) REFERENCES User(id),
    FOREIGN KEY (id_game) REFERENCES Game(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

