CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(255)                            NOT NULL,
    email VARCHAR(512)                            NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);
Create table if not exists request
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    description  varchar(1000)                                       not null,
    requester_id INTEGER references users (id)
);
CREATE TABLE IF NOT EXISTS items
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    name         VARCHAR(255)                                        not null,
    description  varchar(1000)                                       not null,
    is_available boolean                                             not null,
    owner_id     INTEGER REFERENCES users (id),
    request_id   INTEGER references request (id)
);


create table if not exists bookings
(
    id         bigint generated by default as identity PRIMARY KEY not null,
    start_time TIMESTAMP WITHOUT TIME ZONE,
    end_time   TIMESTAMP WITHOUT TIME ZONE,
    item_id    bigint                                              not null,
    booker_id  bigint                                              not null,
    status     VARCHAR(64)                                         not null,
    CONSTRAINT booking_items FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT owner FOREIGN KEY (booker_id) REFERENCES users (id)

);
Create table if not exists request
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    description  varchar(1000)                                       not null,
    requester_id INTEGER references users (id)
);
Create table if not exists comments
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    text      varchar(1000),
    author_id bigint                                              not null,
    item_id   bigint                                              not null,
    created   TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT item FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT author FOREIGN KEY (author_id) REFERENCES users (id)
);
