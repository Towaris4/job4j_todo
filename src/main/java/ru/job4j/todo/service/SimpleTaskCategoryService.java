package ru.job4j.todo.service;

import org.springframework.stereotype.Service;
import ru.job4j.todo.model.TaskCategory;
import ru.job4j.todo.repository.TaskCategoryRepository;

import java.util.Optional;

@Service
public class SimpleTaskCategoryService implements TaskCategoryService {
    private final TaskCategoryRepository taskCategoryRepository;

    public SimpleTaskCategoryService(TaskCategoryRepository taskCategoryRepository) {
        this.taskCategoryRepository = taskCategoryRepository;
    }

    @Override
    public Optional<TaskCategory> create(TaskCategory taskCategory) {
        return taskCategoryRepository.create(taskCategory);
    }

}
