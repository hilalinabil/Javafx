package com.example.doctorhibernate.service.Impl;

import com.example.doctorhibernate.dao.UserDao;
import com.example.doctorhibernate.dao.daoImpl.UserDaoImpl;
import com.example.doctorhibernate.dto.UserDto;
import com.example.doctorhibernate.entities.User;
import com.example.doctorhibernate.service.UserService;
import com.example.doctorhibernate.util.SecurityUtil;

import java.util.Optional;

public class UserServiceImpl implements UserService {

    final UserDao userDao ;
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao ;
        seedAdminUser();
    }

    @Override
    public void seedAdminUser() {
        Optional<User> admin = userDao.findByUserName("admin");

        if (admin.isEmpty()) {
            System.out.println("No Admin found. Creating default Admin...");

            User newAdmin = new User();
            newAdmin.setUsername("admin");
            // Hash the password "admin123" before saving!
            newAdmin.setPasswrd(SecurityUtil.hashPassword("admin123"));
            newAdmin.setRole("ADMIN");
            newAdmin.setEmail("admin@hospital.com");

            userDao.save(newAdmin);
            System.out.println("Default Admin seeded successfully.");
        }
    }
    @Override
    public UserDto login(String username, String plainPassword) throws Exception {
        // 1. Get User from DB
        Optional<User> userOpt = userDao.findByUserName(username);

        if (userOpt.isEmpty()) {
            throw new Exception("User not found");
        }

        User user = userOpt.get();

        // 2. Compare Passwords
        if (!SecurityUtil.checkPassword(plainPassword, user.getPasswrd())) {
            throw new Exception("Wrong password");
        }

        // 3. Return DTO (Hide the entity)
        return new UserDto(user.getUsername(), user.getRole());
    }
}
