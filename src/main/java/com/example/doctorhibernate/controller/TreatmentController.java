package com.example.doctorhibernate.controller;


import com.example.doctorhibernate.entities.Treatment;
import com.example.doctorhibernate.entities.TreatmentType;
import com.example.doctorhibernate.service.Impl.TreatmentServiceImpl;
import com.example.doctorhibernate.service.TreatmentService;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TreatmentController {

    @FXML private TextField txtDescription;
    @FXML private ComboBox<TreatmentType> comboType;
    @FXML private TextField txtCost;
    @FXML private ComboBox<TreatmentType> filterComboType;
    @FXML private TableView<Treatment> treatmentTable;
    @FXML private TableColumn<Treatment, Long> colId;
    @FXML private TableColumn<Treatment, String> colType;
    @FXML private TableColumn<Treatment, String> colDesc;
    @FXML private TableColumn<Treatment, Double> colCost;

    private final TreatmentServiceImpl service = new TreatmentServiceImpl();
    private final ObservableList<Treatment> treatmentData = FXCollections.observableArrayList();
    private FilteredList<Treatment> filteredData;

    @FXML
    public void initialize() {
        ObservableList<TreatmentType> types = FXCollections.observableArrayList(TreatmentType.values());
        comboType.setItems(types);
        filterComboType.setItems(types);

        colId.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getTreatId()));
        colType.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getTreatmentType() != null ? cell.getValue().getTreatmentType().toString() : ""
        ));
        colDesc.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getDescription()));
        colCost.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getCost()));

        treatmentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                comboType.setValue(newVal.getTreatmentType());
                txtDescription.setText(newVal.getDescription());
                txtCost.setText(String.valueOf(newVal.getCost()));
            }
        });

        filteredData = new FilteredList<>(treatmentData, p -> true);

        filterComboType.valueProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(treatment -> {
                if (newValue == null) return true;

                return treatment.getTreatmentType() == newValue;
            });
        });

        treatmentTable.setItems(filteredData);
        refreshTable();
    }

    @FXML
    public void onSave() {
        if (!validateFields()) return;
        try {
            Treatment t = new Treatment();
            t.setTreatmentType(comboType.getValue());
            t.setDescription(txtDescription.getText().trim());
            t.setCost(Double.parseDouble(txtCost.getText().trim()));

            service.addTreatment(t);
            refreshTable();
            clearFields();
            showInfo("Succès", "Traitement enregistré !");
        } catch (Exception e) {
            showError("Erreur", "Erreur d'enregistrement : " + e.getMessage());
        }
    }

    @FXML
    public void onUpdate() {
        Treatment selected = treatmentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (!validateFields()) return;
            try {
                selected.setTreatmentType(comboType.getValue());
                selected.setDescription(txtDescription.getText().trim());
                selected.setCost(Double.parseDouble(txtCost.getText().trim()));

                service.updateTreatment(selected);
                refreshTable();
                clearFields();
                showInfo("Succès", "Mise à jour effectuée !");
            } catch (Exception e) {
                showError("Erreur", "Erreur base de données.");
            }
        } else {
            showWarning("Sélection requise", "Sélectionnez une ligne dans le tableau.");
        }
    }

    @FXML
    public void onDelete() {
        Treatment selected = treatmentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer ce soin ?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        service.removeTreatment(selected);
                        refreshTable();
                        clearFields();
                        showInfo("Succès", "Supprimé.");
                    } catch (Exception e) {
                        showError("Erreur", "Suppression impossible.");
                    }
                }
            });
        }
    }

    @FXML
    public void onClearFilter() {
        filterComboType.setValue(null);
    }

    private void refreshTable() {
        treatmentData.setAll(service.getAllTreatments());
    }

    private boolean validateFields() {
        if (comboType.getValue() == null || txtDescription.getText().trim().isEmpty() || txtCost.getText().trim().isEmpty()) {
            showWarning("Validation", "Tous les champs sont obligatoires.");
            return false;
        }
        try {
            Double.parseDouble(txtCost.getText().trim());
            return true;
        } catch (NumberFormatException e) {
            showError("Erreur", "Le coût doit être un nombre.");
            return false;
        }
    }

    private void clearFields() {
        comboType.setValue(null);
        txtDescription.clear();
        txtCost.clear();
        treatmentTable.getSelectionModel().clearSelection();
    }

    private void showInfo(String t, String c) { new Alert(Alert.AlertType.INFORMATION, c).showAndWait(); }
    private void showWarning(String t, String c) { new Alert(Alert.AlertType.WARNING, c).showAndWait(); }
    private void showError(String t, String c) { new Alert(Alert.AlertType.ERROR, c).showAndWait(); }
}