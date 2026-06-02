CREATE TABLE task_categories (
   id SERIAL PRIMARY KEY,
   task_id INT NOT NULL REFERENCES tasks(id),
   categories_id INT NOT NULL REFERENCES categories(id),
   UNIQUE (task_id, categories_id)
);