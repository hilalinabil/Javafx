package com.example.doctorhibernate.service;

import com.example.doctorhibernate.entities.Doctor;

import java.util.List;

public interface DoctorService {

    void createDoctor(Doctor doctor);

    // READ

    Doctor getDoctorById(Long matr);

    List<Doctor> getAllDoctors();

    List<Doctor> findByFirstName(String firstName);

    List<Doctor> findByLastName(String lastName);

    Doctor findByEmail(String email);

    // UPDATE

    void updateDoctor(Doctor doctor);

    //  DELETE

    void deleteDoctor(Long matr);

    // VALIDATION HELPERS

    boolean isValidEmail(String email);

    boolean emailExists(String email);

    boolean licenseNumberExists(String licenseNumber);
}
