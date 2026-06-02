package ru.job4j.todo.service;

import ru.job4j.todo.model.TaskCategory;

import java.util.Optional;

public interface TaskCategoryService {
    public Optional<TaskCategory> create(TaskCategory taskCategory);
}
