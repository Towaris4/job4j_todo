package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Category;
import ru.job4j.todo.model.Priority;
import ru.job4j.todo.model.Task;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Repository
@AllArgsConstructor
public class HbmCategoryRepository implements CategoryRepository {
    private static final Logger LOG = LoggerFactory.getLogger(HbmCategoryRepository.class);
    private final SessionFactory sf;

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
    public List<Category> findAll() {
        Session session = sf.openSession();
        try {
            return session.createQuery("FROM Category", Category.class)
                    .getResultList();
        } finally {
            session.close();
        }
    }

    @Override
    public Optional<Category> findById(Integer id) {
        Session session = sf.openSession();
        try {
            return Optional.ofNullable(session.createQuery(
                            "FROM Category WHERE id = :fId", Category.class)
                    .setParameter("fId", id)
                    .uniqueResult());
        } finally {
            session.close();
        }
    }
}
