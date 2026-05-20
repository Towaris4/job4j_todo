package ru.job4j.todo.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.service.TaskService;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/tasks")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // 1. Страница со списком + фильтрация
    @GetMapping
    public String getAllTasks(@RequestParam(required = false) String filter, Model model) {
        if (filter == null || filter.equals("all")) {
            model.addAttribute("tasks", taskService.findAll());
        } else if (filter.equals("done")) {
            model.addAttribute("tasks", taskService.findByDone(true));
        } else if (filter.equals("new")) {
            model.addAttribute("tasks", taskService.findByDone(false));
        } else {
            model.addAttribute("tasks", taskService.findAll());
        }
        model.addAttribute("filter", filter); // для подсветки активной ссылки
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
    public String createTask(@ModelAttribute Task task) {
        task.setCreated(LocalDateTime.now());
        task.setDone(false);
        taskService.create(task);
        return "redirect:/tasks";
    }

    // 4. Детальная страница
    @GetMapping("/{id}")
    public String getDetails(@PathVariable Integer id, Model model) {
        Task task = taskService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена"));
        model.addAttribute("task", task);
        return "tasks/details";
    }

    // 7. Форма редактирования
    @GetMapping("/{id}/edit")
    public String getEditForm(@PathVariable Integer id, Model model) {
        Task task = taskService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена"));
        model.addAttribute("task", task);
        return "tasks/form";
    }

    // Обработка обновления
    @PostMapping("/update")
    public String updateTask(@ModelAttribute Task task) {
        taskService.update(task);
        return "redirect:/tasks/" + task.getId();
    }

    // 6. Перевод в состояние "Выполнено"
    @PostMapping("/{id}/done")
    public String markAsDone(@PathVariable Integer id) {
        Task task = taskService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена"));
        task.setDone(true);
        taskService.update(task);
        return "redirect:/tasks/" + id;
    }

    // 8. Удаление
    @PostMapping("/{id}/delete")
    public String deleteTask(@PathVariable Integer id) {
        taskService.delete(id);
        return "redirect:/tasks";
    }
}