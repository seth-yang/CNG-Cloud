CREATE TABLE host_pc (
    id          VARCHAR(48)       NOT NULL PRIMARY KEY,
    shortcut    VARCHAR(16)       NOT NULL,
    name        VARCHAR(128)      NOT NULL,
    mac         VARCHAR(64)       NOT NULL,
    address     VARCHAR(256),
    contact     VARCHAR(16),
    memo        VARCHAR(1024)
);

CREATE TABLE events (
    id          VARCHAR(48)       NOT NULL PRIMARY KEY,
    host_id     VARCHAR(48)       NOT NULL,
    ts          TIMESTAMP         NOT NULL,
    temperature numeric (5, 2),
    humidity    numeric (5, 2)
);