package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.User;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class HbmUserRepository implements UserRepository {

    private static final Logger LOG = LoggerFactory.getLogger(HbmUserRepository.class);
    private final SessionFactory sf;

    @Override
    public Optional<User> save(User user) {
        Session session = sf.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
            return Optional.of(user);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            if (e instanceof org.hibernate.exception.ConstraintViolationException) {
                LOG.error("Пользователь с таким login уже существует: {}", user.getLogin(), e);
            } else {
                LOG.error("Ошибка при сохранении пользователя: login={}", user.getLogin(), e);
            }
            return Optional.empty();
        } finally {
            session.close();
        }
    }

    @Override
    public Optional<User> findByLoginAndPassword(String login, String password) {
        Session session = sf.openSession();
        try {
            return Optional.ofNullable(session.createQuery(
                            "FROM User AS u WHERE u.login = :fLogin AND u.password = :fPassword", User.class)
                    .setParameter("fLogin", login)
                    .setParameter("fPassword", password)
                    .uniqueResult());
        } catch (Exception e) {
            LOG.error("Ошибка при поиске пользователя по login: {}", login, e);
            return Optional.empty();
        } finally {
            session.close();
        }
    }
}