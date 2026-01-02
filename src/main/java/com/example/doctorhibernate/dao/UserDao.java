package com.example.doctorhibernate.dao;

import com.example.doctorhibernate.config.HibernateConfig;
import com.example.doctorhibernate.dao.daoImpl.DoctorDaoImpl;
import com.example.doctorhibernate.dao.daoImpl.UserDaoImpl;
import com.example.doctorhibernate.entities.Patient;
import com.example.doctorhibernate.entities.User;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    Optional<User> findByUserName(String username);
    Optional<User> findByEmail(String email);
    void save(User user);
}
