package com.example.doctorhibernate.dao.daoImpl;

import com.example.doctorhibernate.config.HibernateConfig;
import com.example.doctorhibernate.dao.BillDao;
import com.example.doctorhibernate.entities.Bill;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class BillDaoImpl implements BillDao {

    @Override
    public void save(Bill bill) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(bill);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public List<Bill> findAll() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            return session.createQuery("from Bill", Bill.class).list();
        }
    }

    @Override
    public Bill findById(Long id) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            return session.find(Bill.class, id);
        }
    }

    @Override
    public void delete(Bill bill) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.remove(session.contains(bill) ? bill : session.merge(bill));
            tx.commit();
        }
    }
}
