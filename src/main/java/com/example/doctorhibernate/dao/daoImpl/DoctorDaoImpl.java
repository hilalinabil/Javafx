package com.example.doctorhibernate.dao.daoImpl;

import com.example.doctorhibernate.config.HibernateConfig;
import com.example.doctorhibernate.dao.DoctorDao;
import com.example.doctorhibernate.entities.Doctor;

import org.hibernate.Session;

import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

public class DoctorDaoImpl implements DoctorDao {

    private final static Logger logger = LoggerFactory.getLogger(DoctorDaoImpl.class);

    // Removed field sessionFactory to prevent initialization crash

    @Override
    public void save(Doctor doctor) {
        Session session = null;
        try {
            logger.debug("Saving doctor: {}", doctor.getFirst_name());

            session = HibernateConfig.getSessionFactory().openSession();

            session.beginTransaction();

            doctor.setCreatedAt(LocalDateTime.now());

            session.persist(doctor);

            session.getTransaction().commit();

            logger.info("Doctor saved successfully with ID: {}", doctor.getMatr());

        } catch (Exception e) {

            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
                logger.warn("Transaction rolled back due to error");
            }
            logger.error("Error saving doctor: {}", e.getMessage(), e);
            throw new RuntimeException("Error saving doctor: " + e.getMessage(), e);

        } finally {

            if (session != null && session.isOpen()) {
                session.close();
                logger.debug("Session closed");
            }
        }
    }

    @Override
    public Doctor findById(Long matr) {
        Session session = null;
        try {
            logger.debug("Finding doctor with ID: {}", matr);

            session = HibernateConfig.getSessionFactory().openSession();

            Doctor doctor = session.find(Doctor.class, matr);

            if (doctor != null) {
                logger.info("Doctor found: {}", doctor.getFullName());
            } else {
                logger.warn("Doctor not found with ID: {}", matr);
            }

            return doctor;

        } catch (Exception e) {
            logger.error("Error finding doctor by ID: {}", e.getMessage(), e);
            throw new RuntimeException("Error finding doctor: " + e.getMessage(), e);

        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public List<Doctor> findAll() {
        Session session = null;
        try {
            logger.debug("Finding all doctors");

            session = HibernateConfig.getSessionFactory().openSession();

            Query<Doctor> query = session.createQuery("FROM Doctor ORDER BY id DESC", Doctor.class);

            List<Doctor> doctors = query.list();

            logger.info("Found {} doctors", doctors.size());
            return doctors;

        } catch (Exception e) {
            logger.error("Error finding all doctors: {}", e.getMessage(), e);
            return java.util.Collections.emptyList();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public Doctor findByEmail(String email) {
        Session session = null;
        try {
            logger.debug("Finding doctor with email: {}", email);

            session = HibernateConfig.getSessionFactory().openSession();

            Query<Doctor> query = session.createQuery(
                    "FROM Doctor WHERE email = :email",
                    Doctor.class);

            query.setParameter("email", email);

            Doctor doctor = query.uniqueResult();

            if (doctor != null) {
                logger.info("Doctor found by email: {}", email);
            } else {
                logger.warn("Doctor not found with email: {}", email);
            }

            return doctor;

        } catch (Exception e) {
            logger.error("Error finding doctor by email: {}", e.getMessage(), e);
            throw new RuntimeException("Error finding doctor: " + e.getMessage(), e);

        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public List<Doctor> findByFirstName(String firstName) {
        Session session = null;
        try {
            logger.debug("Finding doctors with first name: {}", firstName);

            session = HibernateConfig.getSessionFactory().openSession();

            Query<Doctor> query = session.createQuery(
                    "FROM Doctor WHERE first_name LIKE :firstName ORDER BY first_name",
                    Doctor.class);

            query.setParameter("firstName", "%" + firstName + "%");

            List<Doctor> doctors = query.list();

            logger.info("Found {} doctors with first name containing: {}", doctors.size(), firstName);
            return doctors;

        } catch (Exception e) {
            logger.error("Error finding doctors by first name: {}", e.getMessage(), e);
            throw new RuntimeException("Error finding doctors: " + e.getMessage(), e);

        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public List<Doctor> findByLastName(String lastName) {
        Session session = null;
        try {
            logger.debug("Finding doctors with last name: {}", lastName);

            session = HibernateConfig.getSessionFactory().openSession();

            Query<Doctor> query = session.createQuery(
                    "FROM Doctor WHERE last_name LIKE :lastName ORDER BY last_name",
                    Doctor.class);

            query.setParameter("lastName", "%" + lastName + "%");

            List<Doctor> doctors = query.list();

            logger.info("Found {} doctors with last name containing: {}", doctors.size(), lastName);
            return doctors;

        } catch (Exception e) {
            logger.error("Error finding doctors by last name: {}", e.getMessage(), e);
            throw new RuntimeException("Error finding doctors: " + e.getMessage(), e);

        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public Doctor findByLicenseNumber(String licenseNumber) {
        Session session = null;
        try {
            logger.debug("Finding doctor with license: {}", licenseNumber);

            session = HibernateConfig.getSessionFactory().openSession();

            Query<Doctor> query = session.createQuery(
                    "FROM Doctor WHERE licenseNumber = :license",
                    Doctor.class);

            query.setParameter("license", licenseNumber);
            Doctor doctor = query.uniqueResult();

            if (doctor != null) {
                logger.info("Doctor found by license: {}", licenseNumber);
            }

            return doctor;

        } catch (Exception e) {
            logger.error("Error finding doctor by license: {}", e.getMessage(), e);
            throw new RuntimeException("Error finding doctor: " + e.getMessage(), e);

        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public void update(Doctor doctor) {
        Session session = null;
        try {
            logger.debug("Updating doctor: {}", doctor.getMatr());

            session = HibernateConfig.getSessionFactory().openSession();
            session.beginTransaction();

            doctor.setUpdatedAt(LocalDateTime.now());

            session.merge(doctor);

            session.getTransaction().commit();

            logger.info("Doctor updated successfully: {}", doctor.getMatr());

        } catch (Exception e) {
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            logger.error("Error updating doctor: {}", e.getMessage(), e);
            throw new RuntimeException("Error updating doctor: " + e.getMessage(), e);

        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public void deleteById(Long matr) {
        Doctor doctor = findById(matr);
        if (doctor != null) {
            delete(doctor);
        } else {
            logger.warn("Cannot delete doctor: ID {} not found", matr);
        }
    }

    // ===== HELPER OPERATIONS =====

    @Override
    public boolean emailExists(String email) {
        return findByEmail(email) != null;
    }

    @Override
    public boolean licenseNumberExists(String licenseNumber) {
        return findByLicenseNumber(licenseNumber) != null;
    }

    @Override
    public long countAll() {
        Session session = null;
        try {
            session = HibernateConfig.getSessionFactory().openSession();

            Query<Long> query = session.createQuery(
                    "SELECT COUNT(*) FROM Doctor",
                    Long.class);

            Long count = query.uniqueResult();

            return count != null ? count : 0;

        } catch (Exception e) {
            logger.error("Error counting doctors: {}", e.getMessage(), e);
            throw new RuntimeException("Error counting doctors: " + e.getMessage(), e);

        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public void delete(Doctor doctor) {
        Session session = null;
        try {
            logger.debug("Deleting doctor: {}", doctor.getMatr());

            session = HibernateConfig.getSessionFactory().openSession();
            session.beginTransaction();

            Doctor managedDoctor = session.merge(doctor);
            session.remove(managedDoctor);

            session.getTransaction().commit();

            logger.info("Doctor deleted successfully: {}", doctor.getMatr());

        } catch (Exception e) {
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            logger.error("Error deleting doctor: {}", e.getMessage(), e);
            throw new RuntimeException("Error deleting doctor: " + e.getMessage(), e);

        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}
