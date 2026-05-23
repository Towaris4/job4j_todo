package ru.job4j.todo.service;

import org.springframework.stereotype.Service;
import ru.job4j.todo.model.Task;

import ru.job4j.todo.repository.TaskRepository;

import java.util.List;
import java.util.Optional;

@Service
public class SimpleTaskService implements TaskService {

    private final TaskRepository taskRepository;

    public SimpleTaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Optional<Task> create(Task task) {
        return taskRepository.create(task);
    }

    @Override
    public boolean update(Task task) {
        return taskRepository.update(task);
    }

    @Override
    public boolean delete(Integer id) {
        return taskRepository.delete(id);
    }

    @Override
    public List<Task> findAll() {
        return taskRepository.findAllOrderById();
    }

    @Override
    public Optional<Task> findById(Integer id) {
        return taskRepository.findById(id);
    }

    @Override
    public List<Task> findByDescriptionLike(String key) {
        return taskRepository.findByDescriptionLike(key);
    }

    @Override
    public List<Task> findByDone(Boolean done) {
        return taskRepository.findByDone(done);
    }

    @Override
    public boolean updateDone(Task task) {
        return taskRepository.updateDone(task);
    }

    @Override
    public List<Task> findFiltered(String filter) {
        if ("done".equalsIgnoreCase(filter)) {
            return findByDone(true);
        }
        if ("new".equalsIgnoreCase(filter)) {
            return findByDone(false);
        }
        return findAll();
    }
}