package com.example.doctorhibernate.dao.daoImpl;

import com.example.doctorhibernate.config.HibernateConfig;
import com.example.doctorhibernate.dao.UserDao;
import com.example.doctorhibernate.entities.Doctor;
import com.example.doctorhibernate.entities.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

    private final static Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    private SessionFactory sessionFactory = HibernateConfig.getSessionFactory();


    @Override
    public Optional<User> findByUserName(String username) {
        logger.debug("Finding user with username: {}", username);

        // 1. Use try-with-resources to automatically close the session
        try (Session session = sessionFactory.openSession()) {

            // 2. Exact match query (removed LIKE and wildcards)
            Query<User> query = session.createQuery(
                    "FROM User WHERE username = :username",
                    User.class // Changed Doctor.class to User.class
            );

            query.setParameter("username", username);

            // 3. Hibernate 5.2+ method to return Optional directly
            Optional<User> result = query.uniqueResultOptional();

            if (result.isPresent()) {
                logger.info("User found: {}", result.get().getUsername());
            } else {
                logger.info("No user found with username: {}", username);
            }

            return result;

        } catch (Exception e) {
            logger.error("Error finding user by username: {}", e.getMessage(), e);
            // It is often better to return Optional.empty() rather than throw in a findBy method,
            // but this depends on your architectural preference.
            throw new RuntimeException("Error finding user: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        logger.debug("Finding user with email: {}", email);

        // 1. Use try-with-resources for auto-closing the session
        try (Session session = sessionFactory.openSession()) {

            // 2. Create query targeting the 'email' field
            Query<User> query = session.createQuery(
                    "FROM User WHERE email = :email",
                    User.class
            );

            // 3. Bind the parameter securely
            query.setParameter("email", email);

            // 4. Return Optional directly (Safe for 0 or 1 result)
            // If your DB allows duplicate emails, this might throw a NonUniqueResultException.
            Optional<User> result = query.uniqueResultOptional();

            if (result.isPresent()) {
                logger.info("User found for email: {}", email);
            } else {
                logger.info("No user found for email: {}", email);
            }

            return result;

        } catch (Exception e) {
            logger.error("Error finding user by email: {}", e.getMessage(), e);
            throw new RuntimeException("Error finding user by email", e);
        }
    }

    @Override
    public void save(User user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            logger.info("User saved successfully: {}", user.getUsername());
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Error saving user", e);
            throw new RuntimeException(e);
        }
    }
}
