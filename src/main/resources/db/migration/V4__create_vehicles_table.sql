CREATE TABLE vehicles (
                          id          BIGSERIAL PRIMARY KEY,
                          plate       VARCHAR(10) NOT NULL,
                          model       VARCHAR(80) NOT NULL,
                          brand       VARCHAR(80) NOT NULL,
                          year        INTEGER NOT NULL,
                          active      BOOLEAN NOT NULL DEFAULT TRUE,
                          created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
                          updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),

                          CONSTRAINT uk_vehicles_plate UNIQUE (plate)
);