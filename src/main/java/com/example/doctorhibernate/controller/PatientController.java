package com.example.doctorhibernate.controller;

import com.example.doctorhibernate.entities.Doctor;
import com.example.doctorhibernate.entities.Patient;
import com.example.doctorhibernate.entities.Room;
import com.example.doctorhibernate.entities.Treatment;
import com.example.doctorhibernate.entities.TreatmentType;
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
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
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
    @FXML private Button saveButton;

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
    private Patient currentEditingPatient = null;

    @FXML
    public void initialize() {
        // 1. Configure Table Columns
        colId.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getId()));
        colFirstName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFirst_name()));
        colLastName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getLast_name()));
        colCin.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCin()));

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
            return new SimpleStringProperty(" Active");
        });

        // 2. Load Data into Selectors
        loadReferenceData();

        // 3. Configure ListView for Multiple Selection
        treatmentList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // 4. Configure ComboBox display (to show Names instead of Objects)
        configureComboBoxes();

        // 5. Load Main Table
        patientTable.setItems(tableData);
        
        // 6. Add selection listener to populate form when patient is selected
        patientTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateForm(newSelection);
            }
        });
        
        refreshTable();
    }

    @FXML
    public void handleSaveButton() {
        try {
            // 1. Validate Selection
            if (roomCombo.getValue() == null) throw new AppException("Please select a Room.");
            if (doctorCombo.getValue() == null) throw new AppException("Please select a Doctor.");

            // 2. Get IDs for Relationships
            Long roomId = roomCombo.getValue().getId();
            Long doctorId = doctorCombo.getValue().getMatr();

            List<Long> treatmentIds = treatmentList.getSelectionModel().getSelectedItems()
                    .stream()
                    .map(Treatment::getTreatId)
                    .collect(Collectors.toList());

            if (currentEditingPatient == null) {
                // CREATE MODE - Add new patient
                Patient p = new Patient();
                p.setFirst_name(firstNameField.getText());
                p.setLast_name(lastNameField.getText());
                p.setCin(cinField.getText());
                p.setEmail(emailField.getText());
                p.setPhone(phoneField.getText());
                p.setAllergies(allergiesField.getText());

                patientService.addPatient(p, roomId, doctorId, treatmentIds);
                showSuccess("Patient registered successfully!");
            } else {
                // UPDATE MODE - Update existing patient
                currentEditingPatient.setFirst_name(firstNameField.getText());
                currentEditingPatient.setLast_name(lastNameField.getText());
                currentEditingPatient.setCin(cinField.getText());
                currentEditingPatient.setEmail(emailField.getText());
                currentEditingPatient.setPhone(phoneField.getText());
                currentEditingPatient.setAllergies(allergiesField.getText());

                // Update relationships
                Room room = roomService.getAllRooms().stream()
                        .filter(r -> r.getId().equals(roomId))
                        .findFirst()
                        .orElse(null);
                if (room != null) {
                    currentEditingPatient.setRoom(room);
                }

                Doctor doctor = doctorService.getAllDoctors().stream()
                        .filter(d -> d.getMatr().equals(doctorId))
                        .findFirst()
                        .orElse(null);
                if (doctor != null) {
                    currentEditingPatient.setDoctor(doctor);
                }

                List<Treatment> treatments = treatmentService.getAllTreatments().stream()
                        .filter(t -> treatmentIds.contains(t.getTreatId()))
                        .collect(Collectors.toList());
                currentEditingPatient.setTreatments(treatments);

                patientService.updatePatient(currentEditingPatient);
                showSuccess("Patient updated successfully!");
            }

            clearFields();
            refreshTable();

        } catch (AppException e) {
            showError(e.getMessage());
        } catch (IllegalArgumentException e) {
            // Catch validation errors from Service (e.g., invalid email, required fields)
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

        // Configure ListView to display Treatment properly
        treatmentList.setCellFactory(param -> new ListCell<Treatment>() {
            @Override
            protected void updateItem(Treatment treatment, boolean empty) {
                super.updateItem(treatment, empty);
                if (empty || treatment == null) {
                    setText(null);
                } else {
                    setText(treatment.getDescription() + " (" + treatment.getTreatmentType() + ") - $" + treatment.getCost());
                }
            }
        });
    }

    private void refreshTable() {
        tableData.setAll(patientService.getAllPatients());
    }

    private void populateForm(Patient patient) {
        currentEditingPatient = patient;
        firstNameField.setText(patient.getFirst_name());
        lastNameField.setText(patient.getLast_name());
        cinField.setText(patient.getCin());
        emailField.setText(patient.getEmail());
        phoneField.setText(patient.getPhone());
        allergiesField.setText(patient.getAllergies());
        
        // Set room, doctor, and treatments
        if (patient.getRoom() != null) {
            roomCombo.setValue(patient.getRoom());
        }
        if (patient.getDoctor() != null) {
            doctorCombo.setValue(patient.getDoctor());
        }
        if (patient.getTreatments() != null && !patient.getTreatments().isEmpty()) {
            treatmentList.getSelectionModel().clearSelection();
            for (Treatment treatment : patient.getTreatments()) {
                int index = treatmentList.getItems().indexOf(treatment);
                if (index >= 0) {
                    treatmentList.getSelectionModel().select(index);
                }
            }
        }
        
        // Update button text
        if (saveButton != null) {
            saveButton.setText("↻ Update Patient");
        }
    }

    @FXML
    public void handleClear() {
        clearFields();
    }

    private void clearFields() {
        currentEditingPatient = null;
        firstNameField.clear();
        lastNameField.clear();
        cinField.clear();
        emailField.clear();
        phoneField.clear();
        allergiesField.clear();
        roomCombo.setValue(null);
        doctorCombo.setValue(null);
        treatmentList.getSelectionModel().clearSelection();
        patientTable.getSelectionModel().clearSelection();
        
        // Reset button text
        if (saveButton != null) {
            saveButton.setText("✓ Register Patient");
        }
    }

    private void showSuccess(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    @FXML
    public void handleAddTreatment() {
        // Create a dialog for adding a new treatment
        Dialog<Treatment> dialog = new Dialog<>();
        dialog.setTitle("Add New Treatment");
        dialog.setHeaderText("Enter treatment details");

        // Set the button types
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Create the form fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Treatment description");

        ComboBox<TreatmentType> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll(TreatmentType.values());
        typeCombo.setPromptText("Select type");

        TextField costField = new TextField();
        costField.setPromptText("Cost (e.g., 100.0)");

        grid.add(new Label("Description:"), 0, 0);
        grid.add(descriptionField, 1, 0);
        grid.add(new Label("Type:"), 0, 1);
        grid.add(typeCombo, 1, 1);
        grid.add(new Label("Cost ($):"), 0, 2);
        grid.add(costField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Enable/Disable add button depending on whether fields are filled
        Button addButton = (Button) dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);

        // Add validation
        descriptionField.textProperty().addListener((observable, oldValue, newValue) -> {
            addButton.setDisable(newValue.trim().isEmpty() || typeCombo.getValue() == null || costField.getText().trim().isEmpty());
        });

        typeCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            addButton.setDisable(descriptionField.getText().trim().isEmpty() || newValue == null || costField.getText().trim().isEmpty());
        });

        costField.textProperty().addListener((observable, oldValue, newValue) -> {
            addButton.setDisable(descriptionField.getText().trim().isEmpty() || typeCombo.getValue() == null || newValue.trim().isEmpty());
        });

        // Convert the result to a Treatment when the add button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    Treatment treatment = new Treatment();
                    treatment.setDescription(descriptionField.getText().trim());
                    treatment.setTreatmentType(typeCombo.getValue());
                    treatment.setCost(Double.parseDouble(costField.getText().trim()));
                    return treatment;
                } catch (NumberFormatException e) {
                    showError("Invalid cost format. Please enter a valid number.");
                    return null;
                }
            }
            return null;
        });

        // Show dialog and handle result
        dialog.showAndWait().ifPresent(treatment -> {
            if (treatment != null) {
                try {
                    treatmentService.addTreatment(treatment);
                    showSuccess("Treatment added successfully!");
                    // Refresh the treatment list
                    loadReferenceData();
                } catch (Exception e) {
                    showError("Failed to add treatment: " + e.getMessage());
                }
            }
        });
    }
}