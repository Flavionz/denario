

CREATE TABLE accounts (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id     VARCHAR(255) NOT NULL,
    iban        VARCHAR(34)  NOT NULL UNIQUE,
    owner_name  VARCHAR(255) NOT NULL,
    balance     DECIMAL(19, 4) NOT NULL DEFAULT 0.0000,
    currency    VARCHAR(3)   NOT NULL DEFAULT 'EUR',
    account_type VARCHAR(20) NOT NULL DEFAULT 'CHECKING',
    status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_accounts_user_id ON accounts(user_id);
CREATE INDEX idx_accounts_iban    ON accounts(iban);
CREATE INDEX idx_accounts_status  ON accounts(status);

COMMENT ON TABLE accounts IS 'Bank accounts associated to Keycloak users';
COMMENT ON COLUMN accounts.user_id IS 'Keycloak subject (sub) claim from JWT';
COMMENT ON COLUMN accounts.balance IS 'Current balance with 4 decimal precision';
