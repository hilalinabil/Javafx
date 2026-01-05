package com.example.doctorhibernate.controller;

import com.example.doctorhibernate.entities.Bill;
import com.example.doctorhibernate.entities.Patient;
import com.example.doctorhibernate.service.Impl.BillServiceImpl;
import com.example.doctorhibernate.service.Impl.PatientServiceImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class BillController {

    @FXML private ComboBox<Patient> patientCombo;
    @FXML private Label totalLabel;
    @FXML private TableView<Bill> billTable;

    @FXML private TableColumn<Bill, Long> colId;
    @FXML private TableColumn<Bill, String> colPatient;
    @FXML private TableColumn<Bill, String> colDate;
    @FXML private TableColumn<Bill, String> colRoom;
    @FXML private TableColumn<Bill, Double> colTotal;
    @FXML private TableColumn<Bill, String> colPaid;

    private final BillServiceImpl billService = new BillServiceImpl();
    private final PatientServiceImpl patientService = new PatientServiceImpl();
    private final ObservableList<Bill> billList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Setup Table
        colId.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getId()));
        colDate.setCellValueFactory(cell -> {
            if (cell.getValue().getIssueDate() != null) {
                return new SimpleStringProperty(cell.getValue().getIssueDate().toString());
            }
            return new SimpleStringProperty("N/A");
        });
        colTotal.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getTotalAmount()));
        colRoom.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getRoomNumber() != null ? cell.getValue().getRoomNumber() : "N/A"));

        colPatient.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getPatient().getCin()));

        colPaid.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().isPaid() ? "Paid" : "Pending"));

        billTable.setItems(billList);

        // Load Data
        loadPatients();
        refreshTable();
    }

    private void loadPatients() {
        // Convert Patient Object to String for ComboBox display
        patientCombo.setItems(FXCollections.observableArrayList(patientService.getAllPatients()));

        // Custom ComboBox Display
        patientCombo.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Patient item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(item.getCin() + " - " + item.getLast_name());
            }
        });
        patientCombo.setButtonCell(patientCombo.getCellFactory().call(null));
    }

    @FXML
    public void handleGenerateBill() {
        Patient selected = patientCombo.getValue();
        if (selected == null) {
            showAlert("Error", "Select a patient first.");
            return;
        }

        Bill newBill = billService.generateBillForPatient(selected);
        billList.add(newBill);
        totalLabel.setText("Total: $" + newBill.getTotalAmount());
        showAlert("Success", "Bill Generated!");
    }

    private void refreshTable() {
        billList.setAll(billService.getAllBills());
    }

    private void showAlert(String title, String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
}