package ru.job4j.todo.repository;

import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.TaskCategory;

import java.util.Optional;

public interface TaskCategoryRepository {
    public Optional<TaskCategory> create(TaskCategory taskCategory);
}
