package com.example.doctorhibernate.controller;

import com.example.doctorhibernate.entities.Doctor;
import com.example.doctorhibernate.service.DoctorService;
import com.example.doctorhibernate.service.Impl.DoctorServiceImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Optional;

public class DoctorController {

    // --- Service ---
    private final DoctorService doctorService = new DoctorServiceImpl();
    private final ObservableList<Doctor> doctorList = FXCollections.observableArrayList();
    private Doctor currentSelectedDoctor = null;

    // --- UI Fields (Form) ---
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField specialityField;
    @FXML private TextField departmentField;
    @FXML private TextField licenseField;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;

    // --- UI Table ---
    @FXML private TableView<Doctor> doctorTable;
    @FXML private TableColumn<Doctor, Long> colMatr;
    @FXML private TableColumn<Doctor, String> colFirstName;
    @FXML private TableColumn<Doctor, String> colLastName;
    @FXML private TableColumn<Doctor, String> colEmail;
    @FXML private TableColumn<Doctor, String> colSpeciality;
    @FXML private TableColumn<Doctor, String> colDepartment;
    @FXML private TableColumn<Doctor, String> colLicense;

    @FXML
    public void initialize() {
        // 1. Configure Columns
        colMatr.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getMatr()));
        colFirstName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFirst_name()));
        colLastName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getLast_name()));
        colEmail.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmail()));
        colSpeciality.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getSpeciality()));
        colDepartment.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDepartment()));

        // Handle potential null license numbers gracefully
        colLicense.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getLicenseNumber() == null ? "-" : cell.getValue().getLicenseNumber())
        );

        // 2. Bind List to Table
        doctorTable.setItems(doctorList);

        // 3. Add Selection Listener (To fill form when clicking a row)
        doctorTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateForm(newSelection);
            }
        });

        // 4. Load Data
        refreshTable();
    }

    @FXML
    public void handleSave() {
        try {
            Doctor doc = new Doctor();
            doc.setFirst_name(firstNameField.getText());
            doc.setLast_name(lastNameField.getText());
            doc.setEmail(emailField.getText());
            doc.setSpeciality(specialityField.getText());
            doc.setDepartment(departmentField.getText());
            doc.setLicenseNumber(licenseField.getText());

            if (currentSelectedDoctor == null) {
                // --- CREATE MODE ---
                doctorService.createDoctor(doc);
                showSuccess("Doctor added successfully!");
            } else {
                // --- UPDATE MODE ---
                // We must set the ID (Matr) so the service knows which one to update
                doc.setMatr(currentSelectedDoctor.getMatr());
                doctorService.updateDoctor(doc);
                showSuccess("Doctor updated successfully!");
            }

            handleClear(); // Reset form
            refreshTable();

        } catch (IllegalArgumentException e) {
            // Catch validation errors from Service (e.g., duplicate email)
            showError(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showError("System Error: " + e.getMessage());
        }
    }

    @FXML
    public void handleDelete() {
        if (currentSelectedDoctor == null) {
            showError("Please select a doctor to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Dr. " + currentSelectedDoctor.getLast_name() + "?");
        confirm.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                doctorService.deleteDoctor(currentSelectedDoctor.getMatr());
                showSuccess("Doctor deleted.");
                handleClear();
                refreshTable();
            } catch (Exception e) {
                showError("Could not delete: " + e.getMessage());
            }
        }
    }

    @FXML
    public void handleClear() {
        currentSelectedDoctor = null;
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        specialityField.clear();
        departmentField.clear();
        licenseField.clear();

        doctorTable.getSelectionModel().clearSelection();
        saveButton.setText("✓ Save Doctor");
        deleteButton.setDisable(true);
    }

    private void populateForm(Doctor doctor) {
        currentSelectedDoctor = doctor;
        firstNameField.setText(doctor.getFirst_name());
        lastNameField.setText(doctor.getLast_name());
        emailField.setText(doctor.getEmail());
        specialityField.setText(doctor.getSpeciality());
        departmentField.setText(doctor.getDepartment());
        licenseField.setText(doctor.getLicenseNumber());

        saveButton.setText("↻ Update Doctor");
        deleteButton.setDisable(false);
    }

    private void refreshTable() {
        doctorList.setAll(doctorService.getAllDoctors());
    }

    private void showSuccess(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Operation Failed");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}