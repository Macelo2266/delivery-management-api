CREATE TABLE addresses (
                           id            BIGSERIAL PRIMARY KEY,
                           street        VARCHAR(150) NOT NULL,
                           number        VARCHAR(10) NOT NULL,
                           complement    VARCHAR(100),
                           neighborhood  VARCHAR(100) NOT NULL,
                           city          VARCHAR(100) NOT NULL,
                           state         VARCHAR(2) NOT NULL,
                           zip_code      VARCHAR(9) NOT NULL
);