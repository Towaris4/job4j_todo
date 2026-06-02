package ru.job4j.todo.repository;

import ru.job4j.todo.model.Category;
import ru.job4j.todo.model.Priority;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    List<Category> findAll();

    public Optional<Category> findById(Integer id);
}
