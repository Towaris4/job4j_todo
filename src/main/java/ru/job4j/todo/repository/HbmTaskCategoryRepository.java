package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.TaskCategory;

import java.util.Optional;
import java.util.function.Function;

@Repository
@AllArgsConstructor
public class HbmTaskCategoryRepository implements TaskCategoryRepository {
    private static final Logger LOG = LoggerFactory.getLogger(HbmTaskCategoryRepository.class);
    private final SessionFactory sf;

    /**
     * Централизованная обработка сессии, транзакции и исключений.
     * Логгирует ошибку, откатывает транзакцию.
     */
    private <T> T tx(Function<Session, T> command) {
        Session session = sf.openSession();
        try {
            session.beginTransaction();
            T result = command.apply(session);
            session.getTransaction().commit();
            return result;
        } catch (Exception e) {
            session.getTransaction().rollback();
            LOG.error("Ошибка выполнения операции в БД", e);
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public Optional<TaskCategory> create(TaskCategory taskCategory) {
        return tx(session -> {
            session.save(taskCategory);
            return Optional.of(taskCategory);
        });
    }
}
