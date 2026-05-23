package ru.job4j.todo.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.service.TaskService;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/tasks")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // 1. Страница со списком + фильтрация
    @GetMapping
    public String getAllTasks(@RequestParam(required = false) String filter, Model model) {
        model.addAttribute("tasks", taskService.findFiltered(filter));
        model.addAttribute("filter", filter);
        return "tasks/list";
    }

    // Форма создания
    @GetMapping("/create")
    public String getCreateForm(Model model) {
        model.addAttribute("task", new Task());
        return "tasks/form";
    }

    // Обработка создания
    @PostMapping("/create")
    public String createTask(@ModelAttribute Task task, Model model) {
        task.setCreated(LocalDateTime.now());
        Optional<Task> optional = taskService.create(task);
        if (optional.isEmpty()) {
            model.addAttribute("task", task);
            model.addAttribute("message", "Ошибка при создании задачи.");
            return "errors/404";
        }
        return "redirect:/tasks";
    }

    // 4. Детальная страница
    @GetMapping("/{id}")
    public String getDetails(@PathVariable Integer id, Model model) {
        Optional<Task> optional = taskService.findById(id);
        if (optional.isEmpty()) {
            model.addAttribute("message", "Задача не найдена");
            return "errors/404";
        }
        Task task = optional.get();
        model.addAttribute("task", task);
        return "tasks/details";
    }

    // 7. Форма редактирования
    @GetMapping("/{id}/edit")
    public String getEditForm(@PathVariable Integer id, Model model) {
        Optional<Task> optional = taskService.findById(id);
        if (optional.isEmpty()) {
            model.addAttribute("message", "Задача не найдена");
            return "errors/404";
        }
        Task task = optional.get();
        model.addAttribute("task", task);
        return "tasks/form";
    }

    // Обработка обновления
    @PostMapping("/update")
    public String updateTask(@ModelAttribute Task task, Model model) {
        if (!taskService.update(task)) {
            model.addAttribute("task", task);
            model.addAttribute("message", "Задача не найдена");
            return "errors/404";
        }
        return "redirect:/tasks/" + task.getId();
    }

    // 6. Перевод в состояние "Выполнено"
    @PostMapping("/{id}/done")
    public String markAsDone(Model model, @PathVariable Integer id) {
        Optional<Task> optional = taskService.findById(id);
        if (optional.isEmpty()) {
            model.addAttribute("message", "Задача не найдена");
            return "errors/404";
        }
        Task task = optional.get();
        taskService.updateDone(task);
        return "redirect:/tasks/" + id;
    }

    // 8. Удаление
    @PostMapping("/{id}/delete")
    public String deleteTask(@PathVariable Integer id, Model model) {
        if (taskService.delete(id)) {
            model.addAttribute("message", "Не удалось обновить задачу");
        }
        return "redirect:/tasks";
    }
}