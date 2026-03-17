-- V1__create_transactions_table.sql

CREATE TABLE transactions (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    source_account_id   UUID         NOT NULL,  -- FK logica (no constraint cross-service!)
    target_account_id   UUID         NOT NULL,
    source_iban         VARCHAR(34)  NOT NULL,
    target_iban         VARCHAR(34)  NOT NULL,
    amount              DECIMAL(19, 4) NOT NULL,
    currency            VARCHAR(3)   NOT NULL DEFAULT 'EUR',
    description         VARCHAR(255),
    status              VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    -- PENDING → COMPLETED o FAILED
    transaction_type    VARCHAR(20)  NOT NULL DEFAULT 'TRANSFER',
    reference           VARCHAR(50)  NOT NULL UNIQUE,  -- Numero riferimento univoco
    initiated_by        VARCHAR(255) NOT NULL,  -- user_id Keycloak
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    completed_at        TIMESTAMP,
    failure_reason      VARCHAR(500)
);

-- ⚠️ NOTA MICROSERVICES: non c'è FK verso la tabella accounts
-- perché accounts è in un altro database (account-service)
-- La consistenza è garantita a livello applicativo + eventi

CREATE INDEX idx_transactions_source_account ON transactions(source_account_id);
CREATE INDEX idx_transactions_target_account ON transactions(target_account_id);
CREATE INDEX idx_transactions_initiated_by   ON transactions(initiated_by);
CREATE INDEX idx_transactions_status         ON transactions(status);
CREATE INDEX idx_transactions_created_at     ON transactions(created_at DESC);

COMMENT ON TABLE transactions IS 'All transfer transactions between accounts';
COMMENT ON COLUMN transactions.reference IS 'Human-readable unique reference (e.g. TXN-20240315-ABC123)';
