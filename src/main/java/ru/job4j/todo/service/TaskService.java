package ru.job4j.todo.service;

import ru.job4j.todo.model.Task;
import java.util.List;
import java.util.Optional;

public interface TaskService {
    Task create(Task task);
    void update(Task task);
    void delete(Integer id);
    List<Task> findAll();
    Optional<Task> findById(Integer id);
    List<Task> findByDescriptionLike(String key);
    List<Task> findByDone(Boolean done);
}