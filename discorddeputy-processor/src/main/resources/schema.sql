CREATE TABLE IF NOT EXISTS discord_notifications
(
    id     TEXT PRIMARY KEY,
    source TEXT,
    data   JSONB
);
