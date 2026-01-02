package com.example.doctorhibernate.controller;

import com.example.doctorhibernate.entities.Doctor;
import com.example.doctorhibernate.entities.Patient;
import com.example.doctorhibernate.entities.Room;
import com.example.doctorhibernate.entities.Treatment;
import com.example.doctorhibernate.exception.AppException;
import com.example.doctorhibernate.service.Impl.DoctorServiceImpl; // Assuming you have this
import com.example.doctorhibernate.service.Impl.PatientServiceImpl;
import com.example.doctorhibernate.service.Impl.RoomServiceImpl;
import com.example.doctorhibernate.service.Impl.TreatmentServiceImpl; // Assuming you have this
import com.example.doctorhibernate.dao.daoImpl.RoomDaoImpl; // Needed for RoomService constructor

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.util.List;
import java.util.stream.Collectors;

public class PatientController {

    // --- Services ---
    private final PatientServiceImpl patientService = new PatientServiceImpl();
    // We instantiate these to populate the ComboBoxes
    private final RoomServiceImpl roomService = new RoomServiceImpl(new RoomDaoImpl());
    private final DoctorServiceImpl doctorService = new DoctorServiceImpl();
    private final TreatmentServiceImpl treatmentService = new TreatmentServiceImpl();

    // --- UI Fields (Input) ---
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField cinField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextArea allergiesField;

    // --- UI Fields (Relationships) ---
    @FXML private ComboBox<Room> roomCombo;
    @FXML private ComboBox<Doctor> doctorCombo;
    @FXML private ListView<Treatment> treatmentList; // Use ListView for multiple selections

    // --- UI Table ---
    @FXML private TableView<Patient> patientTable;
    @FXML private TableColumn<Patient, Long> colId;
    @FXML private TableColumn<Patient, String> colFirstName;
    @FXML private TableColumn<Patient, String> colLastName;
    @FXML private TableColumn<Patient, String> colCin;
    @FXML private TableColumn<Patient, String> colRoom;      // Displays Room Number
    @FXML private TableColumn<Patient, String> colDoctor;    // Displays Doctor Name
    @FXML private TableColumn<Patient, String> colTreatments; // Displays Count or List

    private final ObservableList<Patient> tableData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 1. Configure Table Columns
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("first_name"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("last_name"));
        colCin.setCellValueFactory(new PropertyValueFactory<>("cin"));

        // Custom Cell Factory for Room: Show "101 (ICU)" instead of object
        colRoom.setCellValueFactory(cell -> {
            if (cell.getValue().getRoom() != null) {
                return new SimpleStringProperty(cell.getValue().getRoom().getNumber());
            }
            return new SimpleStringProperty("N/A");
        });

        // Custom Cell Factory for Doctor: Show "Dr. Smith"
        colDoctor.setCellValueFactory(cell -> {
            if (cell.getValue().getDoctor() != null) {
                return new SimpleStringProperty("Dr. " + cell.getValue().getDoctor().getLast_name());
            }
            return new SimpleStringProperty("Unassigned");
        });

        // Custom Cell Factory for Treatments: Show count (e.g., "3 items")
        colTreatments.setCellValueFactory(cell -> {
            int count = cell.getValue().getTreatments().size();
            return new SimpleStringProperty(count + " Active");
        });

        // 2. Load Data into Selectors
        loadReferenceData();

        // 3. Configure ListView for Multiple Selection
        treatmentList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // 4. Configure ComboBox display (to show Names instead of Objects)
        configureComboBoxes();

        // 5. Load Main Table
        patientTable.setItems(tableData);
        refreshTable();
    }

    @FXML
    public void handleSaveButton() {
        try {
            // 1. Validate Selection
            if (roomCombo.getValue() == null) throw new AppException("Please select a Room.");
            if (doctorCombo.getValue() == null) throw new AppException("Please select a Doctor.");

            // 2. Create Basic Patient Object
            Patient p = new Patient();
            p.setFirst_name(firstNameField.getText());
            p.setLast_name(lastNameField.getText());
            p.setCin(cinField.getText());
            p.setEmail(emailField.getText());
            p.setPhone(phoneField.getText());
            p.setAllergies(allergiesField.getText());

            // 3. Get IDs for Relationships
            Long roomId = roomCombo.getValue().getId();
            Long doctorId = doctorCombo.getValue().getMatr();

            List<Long> treatmentIds = treatmentList.getSelectionModel().getSelectedItems()
                    .stream()
                    .map(Treatment::getTreatId) // Assuming Treatment has getId()
                    .collect(Collectors.toList());

            // 4. Call Service
            patientService.addPatient(p, roomId, doctorId, treatmentIds);

            showSuccess("Patient registered successfully!");
            clearFields();
            refreshTable();

        } catch (AppException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showError("System Error: " + e.getMessage());
        }
    }

    @FXML
    public void handleDelete() {
        Patient selected = patientTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a patient to delete.");
            return;
        }
        try {
            patientService.deletePatient(selected.getId());
            refreshTable();
            showSuccess("Patient deleted.");
        } catch (Exception e) {
            showError("Delete failed: " + e.getMessage());
        }
    }

    // --- Helper Methods ---

    private void loadReferenceData() {
        // Populate Room ComboBox
        roomCombo.setItems(FXCollections.observableArrayList(roomService.getAllRooms()));

        // Populate Doctor ComboBox
        doctorCombo.setItems(FXCollections.observableArrayList(doctorService.getAllDoctors()));

        // Populate Treatment List
        treatmentList.setItems(FXCollections.observableArrayList(treatmentService.getAllTreatments()));
    }

    private void configureComboBoxes() {
        // Make Room Combo show "Number - Type"
        roomCombo.setConverter(new StringConverter<Room>() {
            @Override
            public String toString(Room r) {
                return r == null ? "" : r.getNumber() + " (" + r.getType() + ")";
            }
            @Override
            public Room fromString(String string) { return null; } // Not needed for display
        });

        // Make Doctor Combo show "Dr. LastName"
        doctorCombo.setConverter(new StringConverter<Doctor>() {
            @Override
            public String toString(Doctor d) {
                return d == null ? "" : "Dr. " + d.getLast_name();
            }
            @Override
            public Doctor fromString(String string) { return null; }
        });

        // ListView for treatments usually relies on .toString() of the entity,
        // or you can set a CellFactory similar to ComboBox if needed.
    }

    private void refreshTable() {
        tableData.setAll(patientService.getAllPatients());
    }

    private void clearFields() {
        firstNameField.clear();
        lastNameField.clear();
        cinField.clear();
        emailField.clear();
        phoneField.clear();
        allergiesField.clear();
        roomCombo.setValue(null);
        doctorCombo.setValue(null);
        treatmentList.getSelectionModel().clearSelection();
    }

    private void showSuccess(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}