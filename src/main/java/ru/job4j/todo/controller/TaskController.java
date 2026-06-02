package ru.job4j.todo.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.todo.model.*;
import ru.job4j.todo.service.CategoryService;
import ru.job4j.todo.service.PriorityService;
import ru.job4j.todo.service.TaskCategoryService;
import ru.job4j.todo.service.TaskService;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tasks")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final PriorityService priorityService;
    private final CategoryService categoryService;
    private final TaskCategoryService taskCategoryService;


    // 1. Страница со списком + фильтрация
    @GetMapping
    public String getAllTasks(@RequestParam(required = false) String filter, Model model, HttpSession session) {
        model.addAttribute("tasks", taskService.findFiltered(filter));
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("filter", filter);
        return "tasks/list";
    }

    // Форма создания
    @GetMapping("/create")
    public String getCreateForm(Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("priorities", priorityService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        return "tasks/form";
    }

    // Обработка создания
    @PostMapping("/create")
    public String createTask(@ModelAttribute Task task, Model model, HttpSession session, @RequestParam("categoryIds") List<String> ids, @RequestParam("priorityIds") String priorityIds) {
        task.setCreated(LocalDateTime.now());
        TaskCategory taskCategory = new TaskCategory();
        User user = (User) session.getAttribute("user");
        task.setUser(user);
        Priority taskPriority = priorityService.findById(Integer.parseInt(priorityIds.trim()));
        task.setPriority(taskPriority);
        List<Category> categories = categoryService.findAll();
        if (task.getCategories() == null) {
            task.setCategories(new ArrayList<>());
        }
        for (String id : ids) {
            Category category = null;
            if (id != null) {
                category = categories.get(Integer.parseInt(id.trim()) - 1);
            }
            if (category != null) {
                taskCategory.setCategory(category);
                taskCategory.setTask(task);
                task.getCategories().add(category);
            }
        }

        Optional<Task> optional = taskService.create(task);
        if (optional.isEmpty()) {
            model.addAttribute("task", task);
            model.addAttribute("message", "Ошибка при создании задачи.");
            return "errors/404";
        }
        taskCategory.setTask(optional.get());
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
        model.addAttribute("priorities", priorityService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("task", task);
        return "tasks/form";
    }

    // Обработка обновления
    @PostMapping("/update")
    public String updateTask(@ModelAttribute Task task, Model model,  @RequestParam("categoryIds") List<String> ids, @RequestParam("priorityIds") String priorityIds) {
        Task updateTask = taskService.findById(task.getId()).get();
        updateTask.setPriority(priorityService.findById(Integer.parseInt(priorityIds.trim())));
        updateTask.setDone(task.isDone());
        updateTask.setTitle(task.getTitle());
        updateTask.setDescription(task.getDescription());
        TaskCategory taskCategory = new TaskCategory();
        List<Category> categories = categoryService.findAll();
        if (updateTask.getCategories() == null) {
            task.setCategories(new ArrayList<>());
        }
        for (String id : ids) {
            Category category = null;
            if (id != null) {
                category = categories.get(Integer.parseInt(id.trim()) - 1);
            }
            if (category != null) {
                taskCategory.setCategory(category);
                taskCategory.setTask(task);
                taskCategoryService.create(taskCategory);
                updateTask.getCategories().add(category);
            }
        }

        if (!taskService.update(updateTask)) {
            model.addAttribute("task", updateTask);
            model.addAttribute("message", "Задача не обновлена");
            return "errors/404";
        }
        return "redirect:/tasks/" + updateTask.getId();
    }

    // 6. Перевод в состояние "Выполнено"
    @PostMapping("/{id}/done")
    public String arkAsDone(Model model, @PathVariable Integer id) {
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
            model.addAttribute("message", "Не удалось удалить задачу");
        }
        return "redirect:/tasks";
    }
}