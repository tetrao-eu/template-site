--
-- sudo -u postgres psql -p 55555 -d templatesite_db -f conf/database.sql
--
DROP TABLE IF EXISTS message;
DROP TABLE IF EXISTS account;
DROP TYPE IF EXISTS account_role;

CREATE TYPE account_role AS ENUM (
    'normal',
    'admin'
);

CREATE TABLE account (
    id serial PRIMARY KEY,
    name text NOT NULL,
    email text UNIQUE NOT NULL,
    password text NOT NULL,
    role account_role NOT NULL,
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now()
);

CREATE TABLE message (
    id serial PRIMARY KEY,
    content text NOT NULL,
    tag_list text[] NOT NULL,
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now()
);

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO templatesite_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO templatesite_user;

-- bcrypted password values are password in both users
INSERT INTO account (name, email, role, password) values ('Admin User', 'admin@tetrao.eu', 'admin', '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfMQbLgtnOoKsWc.6U6H0llP3puzeeEu');
INSERT INTO account (name, email, role, password) values ('Bob Minion', 'bob@tetrao.eu', 'normal', '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfMQbLgtnOoKsWc.6U6H0llP3puzeeEu');
INSERT INTO message (content, tag_list) values ('Welcome to the templatesite!', '{"welcome", "first message", "english"}');
