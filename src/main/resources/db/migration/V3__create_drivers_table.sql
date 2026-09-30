CREATE TABLE drivers (
                         id              BIGSERIAL PRIMARY KEY,
                         name            VARCHAR(150) NOT NULL,
                         document        VARCHAR(20) NOT NULL,
                         phone           VARCHAR(20) NOT NULL,
                         license_number  VARCHAR(20) NOT NULL,
                         active          BOOLEAN NOT NULL DEFAULT TRUE,
                         created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
                         updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),

                         CONSTRAINT uk_drivers_document UNIQUE (document),
                         CONSTRAINT uk_drivers_license_number UNIQUE (license_number)
);