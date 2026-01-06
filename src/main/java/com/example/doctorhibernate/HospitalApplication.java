package com.example.doctorhibernate;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HospitalApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HospitalApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 2560, 1600);
        stage.setTitle("Hospital Login");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}
