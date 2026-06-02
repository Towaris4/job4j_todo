package ru.job4j.todo.service;

import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    Optional<Task> create(Task task);

    boolean update(Task task);

    boolean delete(Integer id);

    List<Task> findAll();

    Optional<Task> findById(Integer id);

    List<Task> findByDescriptionLike(String key);

    List<Task> findByDone(Boolean done);

    boolean updateDone(Task task);

    List<Task> findFiltered(String filter);

    List<Task> findAllWithCategories();

    List<Task> findAllWithRelations();
}