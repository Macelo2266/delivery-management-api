CREATE TABLE orders (
                        id                     BIGSERIAL PRIMARY KEY,
                        tracking_code          VARCHAR(20) NOT NULL,
                        customer_id            BIGINT NOT NULL,
                        pickup_address_id      BIGINT NOT NULL,
                        delivery_address_id    BIGINT NOT NULL,
                        description            VARCHAR(500) NOT NULL,
                        weight                 NUMERIC(10,2) NOT NULL,
                        status                 VARCHAR(30) NOT NULL,
                        driver_id              BIGINT,
                        created_at             TIMESTAMP NOT NULL DEFAULT NOW(),
                        updated_at             TIMESTAMP NOT NULL DEFAULT NOW(),
                        estimated_delivery_at  TIMESTAMP,
                        delivered_at           TIMESTAMP,

                        CONSTRAINT uk_orders_tracking_code UNIQUE (tracking_code),

                        CONSTRAINT fk_orders_customer
                            FOREIGN KEY (customer_id) REFERENCES customers (id),

                        CONSTRAINT fk_orders_pickup_address
                            FOREIGN KEY (pickup_address_id) REFERENCES addresses (id),

                        CONSTRAINT fk_orders_delivery_address
                            FOREIGN KEY (delivery_address_id) REFERENCES addresses (id),

                        CONSTRAINT fk_orders_driver
                            FOREIGN KEY (driver_id) REFERENCES drivers (id)
);

CREATE INDEX idx_orders_status ON orders (status);
CREATE INDEX idx_orders_customer_id ON orders (customer_id);
CREATE INDEX idx_orders_driver_id ON orders (driver_id);
CREATE INDEX idx_orders_created_at ON orders (created_at);
