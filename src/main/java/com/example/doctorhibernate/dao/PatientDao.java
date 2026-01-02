package com.example.doctorhibernate.dao;

import com.example.doctorhibernate.entities.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientDao {
    Patient save(Patient patient);
    List<Patient> getAllPatients();
    Patient getPatientById(Long id);
    Patient update(Patient patient);
    void delete(Long id);
    Optional<Patient> getPatientByEmail(String email);
    Optional<Patient> getPatientByFullName(String firstName, String lastName);
    Optional<Patient> getPatientByCin(String cin);
    List<Patient> findByRoomId(Long roomId);
    List<Patient> findByDoctorId(Long doctorId);
}
