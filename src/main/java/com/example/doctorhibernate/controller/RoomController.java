package com.example.doctorhibernate.controller;

import com.example.doctorhibernate.dao.daoImpl.RoomDaoImpl;
import com.example.doctorhibernate.entities.Room;
import com.example.doctorhibernate.exception.AppException;
import com.example.doctorhibernate.service.Impl.RoomServiceImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

public class RoomController {

    @FXML
    private TextField numberField;
    @FXML
    private TextField typeField;
    @FXML
    private Spinner<Integer> capacityField;
    @FXML
    private CheckBox occupiedCheck;
    @FXML
    private Button addUpdateButton;

    @FXML
    private TableView<Room> roomTable;
    @FXML
    private TableColumn<Room, Long> colId;
    @FXML
    private TableColumn<Room, String> colNumber;
    @FXML
    private TableColumn<Room, String> colType;
    @FXML
    private TableColumn<Room, Integer> colCapacity;
    @FXML
    private TableColumn<Room, String> colOccupied;
    @FXML
    private TableColumn<Room, Void> colActions;

    private final ObservableList<Room> data = FXCollections.observableArrayList();
    private final RoomServiceImpl service = new RoomServiceImpl(new RoomDaoImpl());
    private Room currentEditingRoom = null;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getId()));
        colNumber.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNumber()));
        colType.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getType()));
        colCapacity.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getCapacity()));

        colOccupied.setCellValueFactory(cellData -> {
            Room room = cellData.getValue();

            // 1. On récupère la liste des patients et la capacité
            // (On ajoute une protection au cas où la liste est null)
            int currentPatients = (room.getPatients() != null) ? room.getPatients().size() : 0;
            int maxCapacity = room.getCapacity(); // Supposons que c'est 4

            // 2. La logique : Est-ce que c'est plein ?
            if (currentPatients >= maxCapacity) {
                return new SimpleStringProperty("Occupied"); // Ou "Complet"
            } else {
                // Affiche "Not Occupied" (ou mieux : "Available 1/4")
                return new SimpleStringProperty("Not Occupied (" + currentPatients + "/" + maxCapacity + ")");
            }
        });

        // Add edit and delete buttons column
        colActions.setCellFactory(param -> new TableCell<>() {
            private final HBox buttonBox = new HBox(5);
            private final Button editButton = new Button("✏️ Edit");
            private final Button deleteButton = new Button("🗑️ Delete");

            {
                buttonBox.setAlignment(Pos.CENTER);

                // Style edit button
                editButton.getStyleClass().addAll("button", "btn-primary");
                editButton.setStyle("-fx-padding: 5 10; -fx-font-size: 11px;");
                editButton.setOnAction(event -> {
                    Room room = getTableView().getItems().get(getIndex());
                    editRoom(room);
                });

                // Style delete button
                deleteButton.getStyleClass().addAll("button", "btn-danger");
                deleteButton.setStyle("-fx-padding: 5 10; -fx-font-size: 11px;");
                deleteButton.setOnAction(event -> {
                    Room room = getTableView().getItems().get(getIndex());
                    deleteRoom(room);
                });

                buttonBox.getChildren().addAll(editButton, deleteButton);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonBox);
                }
            }
        });

        roomTable.setItems(data);

        // Initialize capacity spinner with min 1, max 4, initial value 1
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(
                1, 4, 1);
        capacityField.setValueFactory(valueFactory);

        refresh();
    }

    @FXML
    private void onAddOrUpdateRoom() {
        try {
            int cap = capacityField.getValue();
            if (currentEditingRoom == null) {
                // Add new room
                Room created = service.addRoom(
                        numberField.getText(),
                        typeField.getText(),
                        cap,
                        occupiedCheck.isSelected());
                data.add(created);
                showSuccess("Room added successfully!");
            } else {
                // Update existing room
                Room updated = service.updateRoom(
                        currentEditingRoom.getId(),
                        numberField.getText(),
                        typeField.getText(),
                        cap,
                        occupiedCheck.isSelected());
                // Refresh the table to show updated data
                refresh();
                showSuccess("Room updated successfully!");
            }
            clear();
        } catch (AppException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onRefresh() {
        refresh();
        showInfo("Room list refreshed!");
    }

    private void deleteRoom(Room room) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Room");
        confirmAlert.setContentText(
                "Are you sure you want to delete room " + room.getNumber() + "?\nThis action cannot be undone.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    service.deleteRoom(room.getId());
                    refresh();
                    showSuccess("Room deleted successfully!");
                    // Clear form if editing the deleted room
                    if (currentEditingRoom != null && currentEditingRoom.getId().equals(room.getId())) {
                        clear();
                    }
                } catch (AppException e) {
                    showError(e.getMessage());
                }
            }
        });
    }

    private void editRoom(Room room) {
        currentEditingRoom = room;
        numberField.setText(room.getNumber());
        typeField.setText(room.getType());
        capacityField.getValueFactory().setValue(room.getCapacity());
        occupiedCheck.setSelected(room.isOccupied());
        addUpdateButton.setText("✓ Update Room");
    }

    @FXML
    private void onClear() {
        clear();
    }

    private void refresh() {
        data.setAll(service.getAllRooms());
    }

    private void clear() {
        currentEditingRoom = null;
        numberField.clear();
        typeField.clear();
        capacityField.getValueFactory().setValue(1);
        occupiedCheck.setSelected(false);
        addUpdateButton.setText("✓ Add Room");
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setTitle("Error");
        alert.setHeaderText("Operation Failed");
        alert.showAndWait();
    }

    private void showSuccess(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setTitle("Success");
        alert.setHeaderText("Operation Successful");
        alert.showAndWait();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
