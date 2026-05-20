package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Task;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class HbmTaskRepository implements TaskRepository {
    private final SessionFactory sf;

    public Task create(Task task) {
        Session session = sf.openSession();
        try {
            session.beginTransaction();
            session.save(task);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new RuntimeException("Ошибка создания задачи", e);
        } finally {
            session.close();
        }
        return task;
    }

    public void update(Task task) {
        Session session = sf.openSession();
        try {
            session.beginTransaction();
            session.createQuery(
                            "UPDATE Task SET description = :fDescription, done = :fDone WHERE id = :fId")
                    .setParameter("fDescription", task.getDescription())
                    .setParameter("fDone", task.getDone())
                    .setParameter("fId", task.getId())
                    .executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new RuntimeException("Ошибка обновления задачи", e);
        } finally {
            session.close();
        }
    }

    public void delete(Integer taskId) {
        Session session = sf.openSession();
        try {
            session.beginTransaction();
            session.createQuery("DELETE FROM Task WHERE id = :fId")
                    .setParameter("fId", taskId)
                    .executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new RuntimeException("Ошибка удаления задачи", e);
        } finally {
            session.close();
        }
    }

    public List<Task> findAllOrderById() {
        Session session = sf.openSession();
        try {
            return session.createQuery("FROM Task ORDER BY id ASC", Task.class)
                    .getResultList();
        } finally {
            session.close();
        }
    }

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
}