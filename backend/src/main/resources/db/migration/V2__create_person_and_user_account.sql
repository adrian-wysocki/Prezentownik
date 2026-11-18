CREATE TABLE person (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    display_name VARCHAR(100) NOT NULL
    CHECK (display_name ~ '[^[:space:]]')
    );

CREATE TABLE role(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE CHECK(name ~ '[^[:space:]]')
);

CREATE TABLE user_account (
    person_id BIGINT PRIMARY KEY REFERENCES person(id),
    login VARCHAR(100) NOT NULL UNIQUE
        CHECK (login ~ '[^[:space:]]'),
    password_hash VARCHAR(255) NOT NULL,
    role_id BIGINT REFERENCES role(id) NOT NULL
);

INSERT INTO role (name)
VALUES
    ('ADMIN'),
    ('USER');