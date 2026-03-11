-- V1__create_notifications_table.sql

CREATE TABLE notifications (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         VARCHAR(255) NOT NULL,   -- Keycloak user ID
    type            VARCHAR(50)  NOT NULL,   -- TRANSFER_SENT, TRANSFER_RECEIVED, LOW_BALANCE
    title           VARCHAR(255) NOT NULL,
    message         TEXT         NOT NULL,
    read            BOOLEAN      NOT NULL DEFAULT FALSE,
    related_entity_id   VARCHAR(255),        -- ID transazione correlata
    related_entity_type VARCHAR(50),         -- 'TRANSACTION', 'ACCOUNT'
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    read_at         TIMESTAMP
);

CREATE INDEX idx_notifications_user_id    ON notifications(user_id);
CREATE INDEX idx_notifications_user_read  ON notifications(user_id, read);
CREATE INDEX idx_notifications_created_at ON notifications(created_at DESC);

COMMENT ON TABLE notifications IS 'User notifications pushed via WebSocket and stored for history';
