CREATE TABLE IF NOT EXISTS minecraft_servers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    host VARCHAR(255) NOT NULL,
    port INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS application_users (
    id BIGSERIAL PRIMARY KEY,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    username VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS server_status_history (
    id BIGSERIAL PRIMARY KEY,
    server_id BIGINT NOT NULL REFERENCES minecraft_servers(id),
    online BOOLEAN NOT NULL,
    players_online INTEGER NOT NULL,
    max_players INTEGER NOT NULL,
    latency BIGINT,
    version VARCHAR(255),
    checked_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_status_history_server_id ON server_status_history(server_id);
CREATE INDEX IF NOT EXISTS idx_status_history_checked_at ON server_status_history(checked_at);

CREATE TABLE IF NOT EXISTS server_events (
    id BIGSERIAL PRIMARY KEY,
    server_id BIGINT NOT NULL REFERENCES minecraft_servers(id),
    type VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_events_server_id ON server_events(server_id);
CREATE INDEX IF NOT EXISTS idx_events_created_at ON server_events(created_at);

CREATE TABLE IF NOT EXISTS alert_configs (
    id BIGSERIAL PRIMARY KEY,
    server_id BIGINT NOT NULL REFERENCES minecraft_servers(id),
    channel VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL,
    url VARCHAR(500)
);

CREATE INDEX IF NOT EXISTS idx_alert_configs_server_id ON alert_configs(server_id);
