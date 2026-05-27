package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Priority;

import java.util.List;
import java.util.function.Function;

@Repository
@AllArgsConstructor
public class HbmPriorityRepository implements PriorityRepository {
    private static final Logger LOG = LoggerFactory.getLogger(HbmPriorityRepository.class);
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
    public List<Priority> findAll() {
        Session session = sf.openSession();
        try {
            return session.createQuery("FROM Priority", Priority.class)
                    .getResultList();
        } finally {
            session.close();
        }
    }
}
