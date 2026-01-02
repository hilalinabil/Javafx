package com.example.doctorhibernate.config;


import com.example.doctorhibernate.entities.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




public class HibernateConfig {

    private  static final Logger logger = LoggerFactory.getLogger(HibernateConfig.class);
    private static final SessionFactory sessionFactory ;

    // Static initialization block - runs once when class is loaded
    static
    {
        try{
            logger.info("Initializing Hibernate SessionFactory...");

            // ===== Step 1: Load Configuration =====
            Configuration configuration = new Configuration().configure();

            // ===== Step 2: Register Entity Classes =====
            configuration.addAnnotatedClass(Doctor.class);
            configuration.addAnnotatedClass(User.class);
            configuration.addAnnotatedClass(Patient.class);
            configuration.addAnnotatedClass(Room.class);
            configuration.addAnnotatedClass(Treatment.class);
            configuration.addAnnotatedClass(Bill.class);
            // ===== Step 3: Build SessionFactory =====
            sessionFactory = configuration.buildSessionFactory();

            logger.info("Hibernate SessionFactory created successfully!");

        }catch(Exception e)
        {
            logger.error("Failed to create Hibernate SessionFactory", e);
            e.printStackTrace();

            throw new RuntimeException("Failed to create Hibernate SessionFactory", e);
        }
    }
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        logger.info("Shutting down Hibernate SessionFactory...");
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            logger.info("Hibernate SessionFactory closed successfully!");
        }
    }
}
