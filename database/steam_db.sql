CREATE DATABASE steam_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE steam_db

CREATE TABLE User (
    id int,
    display_name varchar(100),
    PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Game (
    id int,
    description varchar(200),
    PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Recently_Played (
    id_user int,
    id_game int,
    playtime_2weeks int,
    playtime_forever int,
    PRIMARY KEY (id_user, id_game),
    FOREIGN KEY (id_user) REFERENCES User(id),
    FOREIGN KEY (id_game) REFERENCES Game(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Genres (
    id_game int,
    genre varchar(50),
    PRIMARY KEY(id_game),
    FOREIGN KEY(id_game) REFERENCES Game(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Developers (
    id_game int,
    developer varchar(50),
    PRIMARY KEY(id_game),
    FOREIGN KEY(id_game) REFERENCES Game(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE Categories (
    id_game int,
    category varchar(50),
    PRIMARY KEY(id_game),
    FOREIGN KEY(id_game) REFERENCES Game(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;