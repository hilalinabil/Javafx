package com.example.doctorhibernate.service;

import com.example.doctorhibernate.entities.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientService {
    void addPatient(Patient patient, Long roomId, Long doctorId, List<Long> treatmentIds);
    Patient updatePatient(Patient patient) throws Exception;
    void deletePatient(Long id) throws Exception;

    List<Patient> getAllPatients();
    Optional<Patient> findById(Long id);
    List<Patient> searchPatients(String keyword);
}
