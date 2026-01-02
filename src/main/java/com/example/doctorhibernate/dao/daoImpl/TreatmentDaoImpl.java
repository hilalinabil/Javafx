package com.example.doctorhibernate.dao.daoImpl;


import com.example.doctorhibernate.config.HibernateConfig;
import com.example.doctorhibernate.dao.TreatmentDao;
import com.example.doctorhibernate.entities.Treatment;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class TreatmentDaoImpl implements TreatmentDao {

    @Override
    public void save(Treatment treatment) {
        executeInsideTransaction(session -> session.persist(treatment));
    }
    @Override
    public void update(Treatment treatment) {
        executeInsideTransaction(session -> session.merge(treatment));
    }

    @Override
    public void delete(Treatment treatment) {
        executeInsideTransaction(session -> session.remove(treatment));
    }

    @Override
    public Treatment findById(Long id) {
        if (id == null) return null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            return session.find(Treatment.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Treatment> findAll() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            return session.createQuery("from Treatment", Treatment.class).getResultList();
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des traitements:");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    private void executeInsideTransaction(java.util.function.Consumer<Session> action) {
        Transaction tran = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tran = session.beginTransaction();
            action.accept(session);
            tran.commit();
        } catch (Exception e) {
            if (tran != null && tran.isActive()) tran.rollback();
            e.printStackTrace();
            throw new RuntimeException("Erreur Transactionnelle", e);
        }
    }
}