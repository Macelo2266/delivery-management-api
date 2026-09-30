CREATE TABLE delivery_status_history (
                                         id           BIGSERIAL PRIMARY KEY,
                                         order_id     BIGINT NOT NULL,
                                         status       VARCHAR(30) NOT NULL,
                                         description  VARCHAR(300),
                                         changed_at   TIMESTAMP NOT NULL DEFAULT NOW(),
                                         changed_by   VARCHAR(150) NOT NULL,

                                         CONSTRAINT fk_status_history_order
                                             FOREIGN KEY (order_id) REFERENCES orders (id)
);

CREATE INDEX idx_status_history_order_id ON delivery_status_history (order_id);