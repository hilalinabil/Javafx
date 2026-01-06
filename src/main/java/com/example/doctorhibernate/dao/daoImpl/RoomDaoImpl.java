package com.example.doctorhibernate.dao.daoImpl;

import com.example.doctorhibernate.config.HibernateConfig;
import com.example.doctorhibernate.dao.RoomDao;
import com.example.doctorhibernate.entities.Room;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class RoomDaoImpl implements RoomDao {

    @Override
    public void save(Room room) {
        Transaction transaction = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(room);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public Room findById(Long id) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            return session.find(Room.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Room> findAll() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            return session.createQuery("select r from Room r", Room.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    @Override
    public void update(Room room) {
        Transaction transaction = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(room);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Room room) {
        Transaction transaction = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            // We usually need to merge before deleting to attach the object to the current
            // session
            // or simply fetch it by ID then delete if the object is detached.
            session.remove(session.contains(room) ? room : session.merge(room));
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

}
