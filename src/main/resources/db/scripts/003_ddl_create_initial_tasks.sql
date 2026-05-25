CREATE TABLE IF NOT EXISTS tasks (
    id SERIAL PRIMARY KEY,
    title text,
    description TEXT,
    created TIMESTAMP,
    done BOOLEAN
);
rollback DROP tasks;