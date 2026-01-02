
package com.example.doctorhibernate.service.Impl;

import com.example.doctorhibernate.dao.DoctorDao;
import com.example.doctorhibernate.dao.PatientDao;
import com.example.doctorhibernate.dao.RoomDao;
import com.example.doctorhibernate.dao.TreatmentDao;
import com.example.doctorhibernate.dao.daoImpl.DoctorDaoImpl;
import com.example.doctorhibernate.dao.daoImpl.PatientDaoImpl;
import com.example.doctorhibernate.dao.daoImpl.RoomDaoImpl;
import com.example.doctorhibernate.dao.daoImpl.TreatmentDaoImpl;
import com.example.doctorhibernate.entities.Doctor;
import com.example.doctorhibernate.entities.Patient;
import com.example.doctorhibernate.entities.Room;
import com.example.doctorhibernate.entities.Treatment;
import com.example.doctorhibernate.exception.AppException;
import com.example.doctorhibernate.service.PatientService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PatientServiceImpl implements PatientService {

    private final PatientDao patientDao;
    private final DoctorDao doctorDao;
    private final RoomDao roomDao;
    private final TreatmentDao treatmentDao;

    public PatientServiceImpl() {
        this.patientDao = new PatientDaoImpl();
        this.doctorDao = new DoctorDaoImpl();
        this.roomDao = new RoomDaoImpl();
        this.treatmentDao = new TreatmentDaoImpl();
    }

    /**
     * Main method to create a patient.
     * Links the Patient to Room, Doctor, and Treatments based on IDs.
     */
    @Override
    public void addPatient(Patient patient, Long roomId, Long doctorId, List<Long> treatmentIds) {

        // --- 1. VALIDATION: Check Duplicates ---
        if (patient.getCin() != null && !patient.getCin().isEmpty()) {
            Optional<Patient> existingByCin = patientDao.getPatientByCin(patient.getCin());
            if (existingByCin.isPresent()) {
                throw new AppException("A patient with CIN " + patient.getCin() + " already exists.");
            }
        }

        if (patient.getEmail() != null && !patient.getEmail().isEmpty()) {
            Optional<Patient> existingByEmail = patientDao.getPatientByEmail(patient.getEmail());
            if (existingByEmail.isPresent()) {
                throw new AppException("A patient with email " + patient.getEmail() + " already exists.");
            }
        }

        // --- 2. LINKING: Room (with Capacity Check) ---
        Room room = roomDao.findById(roomId);
        if (room == null) {
            throw new AppException("Selected Room not found.");
        }

        // Calculate current occupancy
        long currentOccupants = getCurrentOccupancy(roomId);
        if (room.getCapacity() <= currentOccupants) {
            throw new AppException("Room " + room.getNumber() + " is full! (Capacity: " + room.getCapacity() + ")");
        }

        // Mark room as occupied if it wasn't already (optional logic, depends on your preference)
        if (!room.isOccupied()) {
            room.setOccupied(true);
            roomDao.update(room);
        }

        patient.setRoom(room);

        // --- 3. LINKING: Doctor ---
        Doctor doctor = doctorDao.findById(doctorId);
        if (doctor == null) {
            throw new AppException("Selected Doctor not found.");
        }
        patient.setDoctor(doctor);

        // --- 4. LINKING: Treatments ---
        List<Treatment> treatmentList = new ArrayList<>();
        if (treatmentIds != null && !treatmentIds.isEmpty()) {
            for (Long tId : treatmentIds) {
                Treatment t = treatmentDao.findById(tId);
                if (t != null) {
                    treatmentList.add(t);
                }
            }
        }
        patient.setTreatments(treatmentList);

        // --- 5. SAVE ---
        patientDao.save(patient);
    }

    @Override
    public Patient updatePatient(Patient patient) {
        if (patient.getId() == null) {
            throw new AppException("Cannot update: Patient ID is null.");
        }
        // Note: For updates involving changing Rooms/Doctors, ensure the 'patient' object
        // passed here already has the new Room/Doctor set, OR create a specific update method
        // that accepts IDs like addPatient does.

        patientDao.update(patient);
        return patient;
    }

    @Override
    public void deletePatient(Long id) {
        if (id == null) throw new IllegalArgumentException("ID cannot be null");

        Patient p = patientDao.getPatientById(id);
        if (p == null) throw new AppException("Patient not found.");

        // Optional: If deleting a patient frees up a room, check occupancy logic here
        // But for now, simple delete:
        patientDao.delete(p.getId());
    }

    @Override
    public List<Patient> getAllPatients() {
        return patientDao.getAllPatients();
    }

    @Override
    public Optional<Patient> findById(Long id) {
        return Optional.ofNullable(patientDao.getPatientById(id));
    }

    @Override
    public List<Patient> searchPatients(String keyword) {
        List<Patient> all = patientDao.getAllPatients();

        if (keyword == null || keyword.trim().isEmpty()) {
            return all;
        }

        String lowerText = keyword.toLowerCase();

        return all.stream()
                .filter(p ->
                        (p.getCin() != null && p.getCin().toLowerCase().contains(lowerText)) ||
                                (p.getFirst_name() != null && p.getFirst_name().toLowerCase().contains(lowerText)) ||
                                (p.getLast_name() != null && p.getLast_name().toLowerCase().contains(lowerText)) ||
                                (p.getEmail() != null && p.getEmail().toLowerCase().contains(lowerText))
                )
                .collect(Collectors.toList());
    }

    // --- Helper Method ---

    private long getCurrentOccupancy(Long roomId) {
        // This relies on the method we added to PatientDao earlier
        List<Patient> patientsInRoom = patientDao.findByRoomId(roomId);
        return patientsInRoom != null ? patientsInRoom.size() : 0;
    }
}