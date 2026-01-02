package com.example.doctorhibernate.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import java.io.IOException;
import java.net.URL;

public class MainController {

    @FXML
    private BorderPane mainLayout; // The root layout defined in FXML

    @FXML
    private Label titleLabel; // To update the header title dynamically

    @FXML
    public void initialize() {
        // Load the "Patients" view by default when the app starts
        showPatients();
    }

    // --- Navigation Methods ---

    @FXML
    public void showPatients() {
        loadView("patient-view.fxml", "Patient Management");
    }

    @FXML
    public void showDoctors() {
        loadView("doctor-view.fxml", "Doctor Management");
    }

    @FXML
    public void showRooms() {
        loadView("room-view.fxml", "Room Management");
    }

    @FXML
    public void showTreatments() {
        loadView("treatment-view.fxml", "Treatment Management");
    }

    @FXML
    public void handleLogout() {
        System.out.println("Logging out...");
        // Add logic to close window or show login screen
        System.exit(0);
    }

    @FXML
    public void showBilling() {
        loadView("bill-view.fxml", "Billing & Invoices");
    }
    // --- Helper Method to Swap Views ---

    private void loadView(String fxmlFileName, String title) {
        try {
            // 1. Load the FXML file
            // NOTE: Ensure the path is correct relative to resources
            URL fxmlUrl = getClass().getResource("/com/example/doctorhibernate/" + fxmlFileName);

            // If your FXML files are directly in resources, use: getClass().getResource("/" + fxmlFileName);
            if (fxmlUrl == null) {
                // Fallback for flat structure
                fxmlUrl = getClass().getResource("/" + fxmlFileName);
            }

            if (fxmlUrl == null) {
                System.err.println("Could not find file: " + fxmlFileName);
                return;
            }

            Parent view = FXMLLoader.load(fxmlUrl);

            // 2. Set the view into the Center of the BorderPane
            mainLayout.setCenter(view);

            // 3. Update Title
            if (titleLabel != null) {
                titleLabel.setText(title);
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading view: " + fxmlFileName);
        }
    }
}