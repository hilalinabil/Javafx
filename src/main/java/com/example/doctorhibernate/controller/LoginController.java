package com.example.doctorhibernate.controller;

import com.example.doctorhibernate.dao.daoImpl.UserDaoImpl;
import com.example.doctorhibernate.dto.UserDto;
import com.example.doctorhibernate.service.Impl.UserServiceImpl;
import com.example.doctorhibernate.service.UserService;

// --- CORRECT IMPORTS (JavaFX, not AWT) ---
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final UserService userService;

    public LoginController() {
        // Wired manually for simplicity (No Spring)
        UserDaoImpl userDao = new UserDaoImpl();
        this.userService = new UserServiceImpl(userDao);
    }

    @FXML
    public void handleLoginButton() {
        // 1. Reset error message
        if (errorLabel != null) {
            errorLabel.setVisible(false);
            errorLabel.setText("");
        }

        String username = usernameField.getText();
        String password = passwordField.getText();

        // 2. Validation
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        try {
            // 3. Call the Service
            UserDto userDto = userService.login(username, password);

            System.out.println("Login successful for role: " + userDto.getRole());

            // 4. Redirect based on Role (Assuming all go to main view for now)
            // You can pass the role to the main controller if needed
            openDashboard("main-view.fxml", "Hospital Management System");

        } catch (Exception e) {
            e.printStackTrace();
            showError("Login Failed: " + e.getMessage());
        }
    }

    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
        } else {
            System.err.println("Error Label is null! Message: " + message);
        }
    }

    // Helper method to switch scenes
    private void openDashboard(String fxmlFile, String title) {
        try {
            // 1. Load the FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/doctorhibernate/" + fxmlFile));
            Parent root = loader.load();

            // 2. Get the current stage (window) from any control (e.g., usernameField)
            Stage stage = (Stage) usernameField.getScene().getWindow();

            // 3. Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Could not load dashboard: " + e.getMessage());
        }
    }
}