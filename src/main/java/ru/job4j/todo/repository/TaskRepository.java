package ru.job4j.todo.repository;

import ru.job4j.todo.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {

    Task create(Task task);

    void update(Task task);

    void delete(Integer taskId);

    List<Task> findAllOrderById();

    Optional<Task> findById(Integer taskId);

    List<Task> findByDescriptionLike(String key);

    List<Task> findByDone(Boolean done);
}