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
            URL fxmlUrl = getClass().getResource("/com/example/doctorhibernate/" + fxmlFileName);

            if (fxmlUrl == null) {
                // Fallback
                fxmlUrl = getClass().getResource("/" + fxmlFileName);
            }

            if (fxmlUrl == null) {
                System.err.println("Could not find file: " + fxmlFileName);
                return;
            }

            Parent view = FXMLLoader.load(fxmlUrl);

            // 2. Set Opacity to 0 for Fade In
            view.setOpacity(0);

            // 3. Set the view into the Center of the BorderPane
            mainLayout.setCenter(view);

            // 4. Play Fade Animation
            javafx.animation.FadeTransition fadeTransition = new javafx.animation.FadeTransition(
                    javafx.util.Duration.millis(300), view);
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(1);
            fadeTransition.play();

            // 5. Update Title
            if (titleLabel != null) {
                titleLabel.setText(title);
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading view: " + fxmlFileName);
            // Show error to user to help debugging
            if (mainLayout.getCenter() instanceof javafx.scene.layout.BorderPane) {
                javafx.scene.layout.BorderPane centerPane = (javafx.scene.layout.BorderPane) mainLayout.getCenter();
                if (centerPane.getCenter() instanceof Label) {
                    ((Label) centerPane.getCenter()).setText("Error: " + e.getMessage());
                }
            } else {
                // Fallback if structure changed
                Label errorLabel = new Label("Error loading " + fxmlFileName + ":\n" + e.getCause());
                errorLabel.setStyle("-fx-text-fill: red; -fx-wrap-text: true;");
                mainLayout.setCenter(errorLabel);
            }
        }
    }
}