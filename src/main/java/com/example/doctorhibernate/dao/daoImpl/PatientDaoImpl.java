package com.example.doctorhibernate.dao.daoImpl;

import com.example.doctorhibernate.config.HibernateConfig;
import com.example.doctorhibernate.dao.PatientDao;
import com.example.doctorhibernate.entities.Patient;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatientDaoImpl implements PatientDao {

    private final static Logger logger = LoggerFactory.getLogger(PatientDaoImpl.class);
    private final SessionFactory sessionFactory = HibernateConfig.getSessionFactory();

    @Override
    public Patient save(Patient patient) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            // --- CHANGE HERE: Use merge() instead of persist() ---
            // merge() handles the fact that 'room' and 'doctor' were loaded
            // in a previous session (Detached state).
            Patient savedPatient = session.merge(patient);

            transaction.commit();
            logger.info("Patient saved successfully with ID: {}", savedPatient.getId());
            return savedPatient;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Error saving patient: {}", e.getMessage(), e);
            // It is important to see the 'cause' in the log, which you are doing correctly
            throw new RuntimeException("Error saving patient", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public Patient update(Patient patient) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            Patient updatedPatient = session.merge(patient);

            transaction.commit();
            logger.info("Patient updated: {}", patient.getId());
            return updatedPatient;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Error updating patient: {}", e.getMessage(), e);
            throw new RuntimeException("Error updating patient", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public void delete(Long id) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            Patient patient = session.find(Patient.class, id); // get() is safer than find() for simple lookups
            if (patient != null) {
                session.remove(patient);
                logger.info("Patient deleted with ID: {}", id);
            } else {
                logger.warn("Cannot delete: Patient with ID {} not found", id);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Error deleting patient ID: {}", id, e);
            throw new RuntimeException("Error deleting patient", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    // --- READ-ONLY METHODS (Safe to keep Try-With-Resources) ---
    // These methods don't perform writes, so they don't need transaction rollback logic.

    @Override
    public List<Patient> getAllPatients() {
        try (Session session = sessionFactory.openSession()) {
            logger.debug("Fetching all patients");
            return session.createQuery("FROM Patient", Patient.class).list();
        } catch (Exception e) {
            logger.error("Error fetching all patients", e);
            throw new RuntimeException("Error fetching patients", e);
        }
    }

    @Override
    public Patient getPatientById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Patient patient = session.find(Patient.class, id);
            if (patient == null) {
                logger.warn("No patient found with ID: {}", id);
            }
            return patient;
        } catch (Exception e) {
            logger.error("Error fetching patient by ID: {}", id, e);
            throw new RuntimeException("Error fetching patient", e);
        }
    }

    @Override
    public Optional<Patient> getPatientByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            Query<Patient> query = session.createQuery(
                    "FROM Patient WHERE email = :email", Patient.class
            );
            query.setParameter("email", email);
            return query.uniqueResultOptional();
        } catch (Exception e) {
            logger.error("Error searching patient by email: {}", email, e);
            throw new RuntimeException("Error searching patient", e);
        }
    }

    @Override
    public Optional<Patient> getPatientByFullName(String firstName, String lastName) {
        try (Session session = sessionFactory.openSession()) {
            Query<Patient> query = session.createQuery(
                    "FROM Patient WHERE first_name = :fn AND last_name = :ln", Patient.class
            );
            query.setParameter("fn", firstName);
            query.setParameter("ln", lastName);
            return query.uniqueResultOptional();
        } catch (Exception e) {
            logger.error("Error searching patient by name: {} {}", firstName, lastName, e);
            throw new RuntimeException("Error searching patient", e);
        }
    }

    @Override
    public Optional<Patient> getPatientByCin(String cin) {
        try (Session session = sessionFactory.openSession()) {
            Query<Patient> query = session.createQuery(
                    "FROM Patient WHERE cin = :cin", Patient.class
            );
            query.setParameter("cin", cin);
            return query.uniqueResultOptional();
        } catch (Exception e) {
            logger.error("Error searching patient by CIN: {}", cin, e);
            throw new RuntimeException("Error searching patient", e);
        }
    }

    @Override
    public List<Patient> findByRoomId(Long roomId) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT p FROM Patient p WHERE p.room.id = :rid";
            return session.createQuery(hql, Patient.class)
                    .setParameter("rid", roomId)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Error finding patients by Room ID: {}", roomId, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Patient> findByDoctorId(Long doctorId) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT p FROM Patient p WHERE p.doctor.id = :did";
            return session.createQuery(hql, Patient.class)
                    .setParameter("did", doctorId)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Error finding patients by Doctor ID: {}", doctorId, e);
            return new ArrayList<>();
        }
    }
}