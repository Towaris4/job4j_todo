package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.User;

import java.util.Optional;
import java.util.function.Function;

@Repository
@AllArgsConstructor
public class HbmUserRepository implements UserRepository {

    private static final Logger LOG = LoggerFactory.getLogger(HbmUserRepository.class);
    private final SessionFactory sf;

    /**
     * Универсальный метод-обёртка (Command Pattern) для операций с БД.
     * Принимает лямбду, открывает сессию, управляет транзакцией и закрывает сессию.
     * При ошибке откатывает транзакцию и пробрасывает RuntimeException.
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
    public Optional<User> save(User user) {
        try {
            tx(session -> {
                session.save(user);
                return user;
            });
            return Optional.of(user);
        } catch (RuntimeException e) {
            if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
                LOG.error("Пользователь с таким login уже существует: {}", user.getLogin(), e);
            } else {
                LOG.error("Ошибка при сохранении пользователя: login={}", user.getLogin(), e);
            }
            return Optional.empty();
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