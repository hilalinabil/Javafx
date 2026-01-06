
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PatientServiceImpl implements PatientService {

    private static final Logger logger = LoggerFactory.getLogger(PatientServiceImpl.class);

    // Email validation pattern (same as DoctorService)
    private static final String EMAIL_PATTERN =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);

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

        // --- 0. VALIDATION: Required Fields ---
        if (patient.getFirst_name() == null || patient.getFirst_name().trim().isEmpty()) {
            logger.warn("Validation failed: First name is required");
            throw new IllegalArgumentException("First name is required");
        }

        if (patient.getLast_name() == null || patient.getLast_name().trim().isEmpty()) {
            logger.warn("Validation failed: Last name is required");
            throw new IllegalArgumentException("Last name is required");
        }

        if (patient.getCin() == null || patient.getCin().trim().isEmpty()) {
            logger.warn("Validation failed: CIN is required");
            throw new IllegalArgumentException("CIN is required");
        }

        if (patient.getEmail() == null || patient.getEmail().trim().isEmpty()) {
            logger.warn("Validation failed: Email is required");
            throw new IllegalArgumentException("Email is required");
        }

        if (patient.getPhone() == null || patient.getPhone().trim().isEmpty()) {
            logger.warn("Validation failed: Phone number is required");
            throw new IllegalArgumentException("Phone number is required");
        }

        // Validate email format
        if (!isValidEmail(patient.getEmail())) {
            logger.warn("Validation failed: Invalid email format: {}", patient.getEmail());
            throw new IllegalArgumentException("Invalid email format");
        }

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

        // --- VALIDATION: Required Fields ---
        if (patient.getFirst_name() == null || patient.getFirst_name().trim().isEmpty()) {
            logger.warn("Validation failed: First name is required");
            throw new IllegalArgumentException("First name is required");
        }

        if (patient.getLast_name() == null || patient.getLast_name().trim().isEmpty()) {
            logger.warn("Validation failed: Last name is required");
            throw new IllegalArgumentException("Last name is required");
        }

        if (patient.getCin() == null || patient.getCin().trim().isEmpty()) {
            logger.warn("Validation failed: CIN is required");
            throw new IllegalArgumentException("CIN is required");
        }

        if (patient.getEmail() == null || patient.getEmail().trim().isEmpty()) {
            logger.warn("Validation failed: Email is required");
            throw new IllegalArgumentException("Email is required");
        }

        if (patient.getPhone() == null || patient.getPhone().trim().isEmpty()) {
            logger.warn("Validation failed: Phone number is required");
            throw new IllegalArgumentException("Phone number is required");
        }

        // Validate email format
        if (!isValidEmail(patient.getEmail())) {
            logger.warn("Validation failed: Invalid email format: {}", patient.getEmail());
            throw new IllegalArgumentException("Invalid email format");
        }

        // Check email uniqueness (exclude current patient)
        Optional<Patient> existingByEmail = patientDao.getPatientByEmail(patient.getEmail().trim());
        if (existingByEmail.isPresent() && !existingByEmail.get().getId().equals(patient.getId())) {
            logger.warn("Validation failed: Email already exists: {}", patient.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }

        // Check CIN uniqueness (exclude current patient)
        Optional<Patient> existingByCin = patientDao.getPatientByCin(patient.getCin().trim());
        if (existingByCin.isPresent() && !existingByCin.get().getId().equals(patient.getId())) {
            logger.warn("Validation failed: CIN already exists: {}", patient.getCin());
            throw new IllegalArgumentException("CIN already exists");
        }

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

    // --- Helper Methods ---

    private long getCurrentOccupancy(Long roomId) {
        // This relies on the method we added to PatientDao earlier
        List<Patient> patientsInRoom = patientDao.findByRoomId(roomId);
        return patientsInRoom != null ? patientsInRoom.size() : 0;
    }

    /**
     * Validates email format using regex pattern
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return emailPattern.matcher(email.trim()).matches();
    }
}