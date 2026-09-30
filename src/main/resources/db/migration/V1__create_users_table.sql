CREATE TABLE users (
                       id            BIGSERIAL PRIMARY KEY,
                       name          VARCHAR(120) NOT NULL,
                       email         VARCHAR(150) NOT NULL,
                       password      VARCHAR(255) NOT NULL,
                       role          VARCHAR(20) NOT NULL,
                       active        BOOLEAN NOT NULL DEFAULT TRUE,
                       customer_id   BIGINT,
                       driver_id     BIGINT,
                       created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
                       updated_at    TIMESTAMP NOT NULL DEFAULT NOW(),

                       CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE INDEX idx_users_role ON users (role);