package com.example.doctorhibernate.dao;

import com.example.doctorhibernate.entities.Doctor;

import java.util.List;

public interface DoctorDao {

    void save(Doctor doctor);

    Doctor findById(Long matr);

    List<Doctor> findAll();

    Doctor findByEmail(String email);

    List<Doctor> findByFirstName(String firstName);


    List<Doctor> findByLastName(String lastName);


    Doctor findByLicenseNumber(String licenseNumber);

    void update(Doctor doctor);

    void deleteById(Long matr);

    void delete(Doctor doctor);

    boolean emailExists(String email);

    boolean licenseNumberExists(String licenseNumber);

    long countAll();


}
