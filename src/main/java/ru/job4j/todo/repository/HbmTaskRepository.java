package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Task;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Repository
@AllArgsConstructor
public class HbmTaskRepository implements TaskRepository {
    private static final Logger LOG = LoggerFactory.getLogger(HbmTaskRepository.class);
    private final SessionFactory sf;

    /**
     * Централизованная обработка сессии, транзакции и исключений.
     * Логгирует ошибку, откатывает транзакцию и пробрасывает RuntimeException дальше.
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
            throw new RuntimeException("Ошибка работы с базой данных", e);
        } finally {
            session.close();
        }
    }

    @Override
    public Optional<Task> create(Task task) {
        return tx(session -> {
            session.save(task);
            return Optional.of(task);
        });
    }

    @Override
    public boolean update(Task task) {
        return tx(session -> session.createQuery(
                        "UPDATE Task SET title = :fTitle, description = :fDescription, done = :fDone WHERE id = :fId")
                .setParameter("fTitle", task.getTitle())
                .setParameter("fDescription", task.getDescription())
                .setParameter("fDone", task.isDone())
                .setParameter("fId", task.getId())
                .executeUpdate() > 0);
    }

    @Override
    public boolean updateDone(Task task) {
        return tx(session -> session.createQuery(
                        "UPDATE Task SET done = :fDone WHERE id = :fId")
                .setParameter("fDone", task.isDone()) // ⚠️ Было: task.setDone(true) захардкожено
                .setParameter("fId", task.getId())
                .executeUpdate() > 0);
    }

    @Override
    public boolean delete(Integer taskId) {
        return tx(session -> session.createQuery("DELETE FROM Task WHERE id = :fId")
                .setParameter("fId", taskId)
                .executeUpdate() > 0);
    }

    @Override
    public List<Task> findAllOrderById() {
        // Запросы без изменения данных транзакцией не оборачиваются
        Session session = sf.openSession();
        try {
            return session.createQuery("FROM Task ORDER BY id ASC", Task.class)
                    .getResultList();
        } finally {
            session.close();
        }
    }

    @Override
    public Optional<Task> findById(Integer taskId) {
        Session session = sf.openSession();
        try {
            return Optional.ofNullable(session.createQuery(
                            "FROM Task AS t WHERE t.id = :fId", Task.class)
                    .setParameter("fId", taskId)
                    .uniqueResult());
        } finally {
            session.close();
        }
    }

    @Override
    public List<Task> findByDescriptionLike(String key) {
        Session session = sf.openSession();
        try {
            return session.createQuery(
                            "FROM Task AS t WHERE t.description LIKE :fKey", Task.class)
                    .setParameter("fKey", "%" + key + "%")
                    .getResultList();
        } finally {
            session.close();
        }
    }

    @Override
    public List<Task> findByDone(Boolean done) {
        Session session = sf.openSession();
        try {
            return session.createQuery(
                            "FROM Task AS t WHERE t.done = :fDone", Task.class)
                    .setParameter("fDone", done)
                    .getResultList();
        } finally {
            session.close();
        }
    }

    @Override
    public List<Task> findFiltered(String filter) {
        if ("done".equalsIgnoreCase(filter)) {
            return findByDone(true);
        }
        if ("new".equalsIgnoreCase(filter)) {
            return findByDone(false);
        }
        return findAllOrderById();
    }
}