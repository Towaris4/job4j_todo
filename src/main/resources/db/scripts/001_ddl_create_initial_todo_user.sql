CREATE TABLE IF NOT EXISTS todo_user (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    login VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);
rollback DROP todo_user;