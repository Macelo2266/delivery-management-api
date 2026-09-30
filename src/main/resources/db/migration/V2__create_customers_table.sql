CREATE TABLE customers (
                           id           BIGSERIAL PRIMARY KEY,
                           name         VARCHAR(150) NOT NULL,
                           document     VARCHAR(20) NOT NULL,
                           email        VARCHAR(150) NOT NULL,
                           phone        VARCHAR(20) NOT NULL,
                           created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
                           updated_at   TIMESTAMP NOT NULL DEFAULT NOW(),

                           CONSTRAINT uk_customers_document UNIQUE (document)
);