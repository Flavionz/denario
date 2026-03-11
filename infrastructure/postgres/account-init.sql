-- Questo file viene eseguito SOLO la prima volta che il container viene creato
-- Dopo, Flyway gestisce le migrazioni successive (V2__, V3__, ecc.)

-- Le tabelle vere vengono create da Flyway (src/main/resources/db/migration/)
-- Questo script è solo per setup iniziale se necessario

-- Estensioni PostgreSQL utili per un'app bancaria
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";   -- Generazione UUID
CREATE EXTENSION IF NOT EXISTS "pgcrypto";    -- Funzioni di hashing
