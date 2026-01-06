package com.example.doctorhibernate.service.Impl;

import com.example.doctorhibernate.dao.DoctorDao;
import com.example.doctorhibernate.dao.daoImpl.DoctorDaoImpl;
import com.example.doctorhibernate.entities.Doctor;
import com.example.doctorhibernate.service.DoctorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Pattern;

public class DoctorServiceImpl implements DoctorService {

    private static final Logger logger = LoggerFactory.getLogger(DoctorServiceImpl.class);

    private DoctorDao doctorDAO = new DoctorDaoImpl();

    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);

    @Override
    public void createDoctor(Doctor req) {

        logger.info("Creating doctor: {} {}", req.getFirst_name(), req.getLast_name());

        if (req.getFirst_name() == null || req.getFirst_name().trim().isEmpty()) {
            logger.warn("Validation failed: First name is required");
            throw new IllegalArgumentException("First name is required");
        }

        if (req.getLast_name() == null || req.getLast_name().trim().isEmpty()) {
            logger.warn("Validation failed: Last name is required");
            throw new IllegalArgumentException("Last name is required");
        }

        if (req.getEmail() == null || req.getEmail().trim().isEmpty()) {
            logger.warn("Validation failed: Email is required");
            throw new IllegalArgumentException("Email is required");
        }

        if (req.getSpeciality() == null || req.getSpeciality().trim().isEmpty()) {
            logger.warn("Validation failed: Speciality is required");
            throw new IllegalArgumentException("Speciality is required");
        }

        if (req.getDepartment() == null || req.getDepartment().trim().isEmpty()) {
            logger.warn("Validation failed: department is required");
            throw new IllegalArgumentException("department is required");
        }

        if (!isValidEmail(req.getEmail())) {
            logger.warn("Validation failed: Invalid email format: {}", req.getEmail());
            throw new IllegalArgumentException("Invalid email format");
        }

        if (emailExists(req.getEmail().trim())) {
            logger.warn("Validation failed: Email already exists: {}", req.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }

        // License number must be unique (if provided)
        if (req.getLicenseNumber() != null && !req.getLicenseNumber().trim().isEmpty()) {
            if (licenseNumberExists(req.getLicenseNumber().trim())) {
                logger.warn("Validation failed: License number already exists: {}", req.getLicenseNumber());
                throw new IllegalArgumentException("License number already exists");
            }
        }

        Doctor doctor = new Doctor();
        doctor.setFirst_name(req.getFirst_name().trim());
        doctor.setLast_name(req.getLast_name().trim());
        doctor.setEmail(req.getEmail().trim());
        doctor.setSpeciality(req.getSpeciality().trim());
        doctor.setDepartment(req.getDepartment().trim());
        doctor.setLicenseNumber(req.getLicenseNumber());

        doctorDAO.save(doctor);

        logger.info("Doctor created successfully with ID: {}", doctor.getMatr());
    }

    // READ OPERATIONS

    @Override
    public Doctor getDoctorById(Long matr) {
        if (matr == null || matr <= 0) {
            logger.warn("Invalid ID: {}", matr);
            throw new IllegalArgumentException("Valid doctor Matricule required");
        }

        Doctor doctor = doctorDAO.findById(matr);

        if (doctor == null) {
            logger.warn("Doctor not found with Matricule: {}", matr);
        }

        return doctor;
    }

    @Override
    public List<Doctor> getAllDoctors() {
        logger.info("Fetching all doctors");
        List<Doctor> doctors = doctorDAO.findAll();
        logger.info("Found {} doctors", doctors.size());
        return doctors;
    }

    @Override
    public List<Doctor> findByFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            logger.warn("Search failed: First name cannot be empty");
            throw new IllegalArgumentException("First name cannot be empty");
        }

        logger.info("Searching doctors by first name: {}", firstName);
        List<Doctor> doctors = doctorDAO.findByFirstName(firstName.trim());
        logger.info("Found {} doctors with first name: {}", doctors.size(), firstName);
        return doctors;
    }

    @Override
    public List<Doctor> findByLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            logger.warn("Search failed: Last name cannot be empty");
            throw new IllegalArgumentException("Last name cannot be empty");
        }

        logger.info("Searching doctors by last name: {}", lastName);
        List<Doctor> doctors = doctorDAO.findByLastName(lastName.trim());
        logger.info("Found {} doctors with last name: {}", doctors.size(), lastName);
        return doctors;
    }

    @Override
    public Doctor findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            logger.warn("Search failed: Email cannot be empty");
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if (!isValidEmail(email)) {
            logger.warn("Search failed: Invalid email format: {}", email);
            throw new IllegalArgumentException("Invalid email format");
        }

        logger.info("Searching doctor by email: {}", email);
        Doctor doctor = doctorDAO.findByEmail(email.trim());

        if (doctor == null) {
            logger.warn("Doctor not found with email: {}", email);
        }

        return doctor;
    }

    // UPDATE OPERATION

    @Override
    public void updateDoctor(Doctor req) {

        logger.info("Updating doctor: {}", req.getMatr());

        // ===== VALIDATION: ID =====
        if (req.getMatr() == null || req.getMatr() <= 0) {
            logger.warn("Validation failed: Valid ID required");
            throw new IllegalArgumentException("Valid doctor ID required");
        }

        // ===== VALIDATION: REQUIRED FIELDS =====
        if (req.getFirst_name() == null || req.getFirst_name().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }

        if (req.getLast_name() == null || req.getLast_name().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }

        if (req.getEmail() == null || req.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (!isValidEmail(req.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // ===== VALIDATION: EMAIL UNIQUENESS (exclude current doctor) =====
        Doctor existingDoctor = doctorDAO.findByEmail(req.getEmail().trim());
        if (existingDoctor != null && !existingDoctor.getMatr().equals(req.getMatr())) {
            logger.warn("Email already used by another doctor");
            throw new IllegalArgumentException("Email already exists");
        }

        // ===== VALIDATION: LICENSE UNIQUENESS (exclude current doctor) =====
        if (req.getLicenseNumber() != null && !req.getLicenseNumber().trim().isEmpty()) {
            Doctor existingLicense = doctorDAO.findByLicenseNumber(req.getLicenseNumber().trim());
            if (existingLicense != null && !existingLicense.getMatr().equals(req.getMatr())) {
                logger.warn("License number already used by another doctor");
                throw new IllegalArgumentException("License number already exists");
            }
        }

        // ===== FETCH AND UPDATE =====
        Doctor newdoc = doctorDAO.findById(req.getMatr());

        if (newdoc == null) {
            logger.warn("Doctor not found: {}", newdoc.getMatr());
            throw new IllegalArgumentException("Doctor not found");
        }

        newdoc.setFirst_name(req.getFirst_name().trim());
        newdoc.setLast_name(req.getLast_name().trim());
        newdoc.setEmail(req.getEmail().trim());
        newdoc.setSpeciality(req.getSpeciality().trim());
        newdoc.setDepartment(req.getDepartment().trim());
        newdoc.setLicenseNumber(req.getLicenseNumber() != null ? req.getLicenseNumber().trim() : null);

        doctorDAO.update(newdoc);

        logger.info("Doctor updated successfully: {}", newdoc.getMatr());
    }

    // DELETE OPERATION

    @Override
    public void deleteDoctor(Long matr) {
        logger.info("Deleting doctor: {}", matr);

        if (matr == null || matr <= 0) {
            logger.warn("Validation failed: Valid ID required for delete");
            throw new IllegalArgumentException("Valid doctor ID required");
        }

        Doctor doctor = doctorDAO.findById(matr);

        if (doctor == null) {
            logger.warn("Doctor not found: {}", matr);
            throw new IllegalArgumentException("Doctor not found");
        }

        // 1. Unassign all patients from this doctor
        com.example.doctorhibernate.dao.PatientDao patientDao = new com.example.doctorhibernate.dao.daoImpl.PatientDaoImpl();
        List<com.example.doctorhibernate.entities.Patient> patients = patientDao.findByDoctorId(matr);

        for (com.example.doctorhibernate.entities.Patient p : patients) {
            p.setDoctor(null); // Unassign
            patientDao.update(p);
        }

        // 2. Delete the doctor
        doctorDAO.delete(doctor);

        logger.info("Doctor deleted successfully: {}", matr);
    }

    // VALIDATION HELPER METHODS

    @Override
    public boolean isValidEmail(String email) {
        return email != null && emailPattern.matcher(email).matches();
    }

    @Override
    public boolean emailExists(String email) {
        return doctorDAO.findByEmail(email) != null;
    }

    @Override
    public boolean licenseNumberExists(String licenseNumber) {
        if (licenseNumber == null || licenseNumber.trim().isEmpty()) {
            return false;
        }
        return doctorDAO.findByLicenseNumber(licenseNumber) != null;
    }
}
