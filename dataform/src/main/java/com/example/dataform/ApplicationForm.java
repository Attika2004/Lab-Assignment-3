package com.example.dataform;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ApplicationForm extends Application {

    private static final String FILE_NAME = "records.txt";

    // Fields
    private TextField fullNameField;
    private TextField idField;
    private TextField provinceField;
    private DatePicker dobPicker;
    private ToggleGroup genderGroup;
    private Label statusLabel;

    private List<String[]> records = new ArrayList<>();
    private int currentRecordIndex = -1;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Application Form");

        // Left Pane - Input Fields
        VBox leftPane = createInputFields();

        // Right Pane - Buttons
        VBox rightPane = createButtons();

        // Main Layout
        HBox mainLayout = new HBox(20, leftPane, rightPane);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: #3584c4; -fx-text-fill: white" + ";");

        Scene scene = new Scene(mainLayout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createInputFields() {
        fullNameField = new TextField();
        idField = new TextField();
        provinceField = new TextField();
        dobPicker = new DatePicker();

        // Gender Radio Buttons
        genderGroup = new ToggleGroup();
        RadioButton maleButton = new RadioButton("Male");
        RadioButton femaleButton = new RadioButton("Female");
        maleButton.setToggleGroup(genderGroup);
        femaleButton.setToggleGroup(genderGroup);
        maleButton.setStyle("-fx-text-fill: blue;");
        femaleButton.setStyle("-fx-text-fill: blue;");
        HBox genderBox = new HBox(10, maleButton, femaleButton);

        // Styling
        fullNameField.setPromptText("Full Name");
        idField.setPromptText("ID");
        provinceField.setPromptText("Home Province");
        fullNameField.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");
        idField.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");
        provinceField.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");
        dobPicker.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");

        VBox vbox = new VBox(15, new Label("Full Name"), fullNameField,
                new Label("ID"), idField,
                new Label("Gender"), genderBox,
                new Label("Home Province"), provinceField,
                new Label("Date of Birth"), dobPicker);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER_LEFT);
        vbox.setStyle("-fx-background-color: #4d687d; -fx-text-fill: white;");

        return vbox;
    }

    private VBox createButtons() {
        Button newButton = new Button("New");
        Button deleteButton = new Button("Delete");
        Button restoreButton = new Button("Restore");
        Button findNextButton = new Button("Find Next");
        Button findPrevButton = new Button("Find Prev");
        Button closeButton = new Button("Close");

        // Style Buttons
        String buttonStyle = "-fx-background-color: #444; -fx-text-fill: white ;";
        newButton.setStyle(buttonStyle);
        deleteButton.setStyle(buttonStyle);
        restoreButton.setStyle(buttonStyle);
        findNextButton.setStyle(buttonStyle);
        findPrevButton.setStyle(buttonStyle);
        closeButton.setStyle(buttonStyle);

        // Disable inactive buttons
        deleteButton.setDisable(true);
        restoreButton.setDisable(true);
        findNextButton.setDisable(true);
        findPrevButton.setDisable(true);

        // Event Handlers
        newButton.setOnAction(e -> saveRecord());
        closeButton.setOnAction(e -> closeApplication());
        findNextButton.setOnAction(e -> findRecord());

        VBox vbox = new VBox(15, newButton, deleteButton, restoreButton, findNextButton, findPrevButton, closeButton);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER_RIGHT);
        return vbox;
    }

    private void saveRecord() {
        String fullName = fullNameField.getText();
        String id = idField.getText();
        String province = provinceField.getText();
        String dob = (dobPicker.getValue() != null) ? dobPicker.getValue().toString() : "";
        RadioButton selectedGender = (RadioButton) genderGroup.getSelectedToggle();
        String gender = (selectedGender != null) ? selectedGender.getText() : "";

        if (fullName.isEmpty() || id.isEmpty() || province.isEmpty() || dob.isEmpty() || gender.isEmpty()) {
            showAlert("Validation Error", "All fields must be filled out.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(String.join(",", fullName, id, gender, province, dob));
            writer.newLine();
            showAlert("Success", "Record saved successfully.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void findRecord() {
        String idToFind = idField.getText();
        if (idToFind.isEmpty()) {
            showAlert("Validation Error", "Please enter an ID to search.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            records.clear();
            while ((line = reader.readLine()) != null) {
                String[] record = line.split(",");
                records.add(record);
            }

            currentRecordIndex = -1;
            for (int i = 0; i < records.size(); i++) {
                if (records.get(i)[1].equals(idToFind)) {
                    currentRecordIndex = i;
                    break;
                }
            }

            if (currentRecordIndex != -1) {
                String[] record = records.get(currentRecordIndex);
                fullNameField.setText(record[0]);
                idField.setText(record[1]);
                provinceField.setText(record[3]);
                dobPicker.setValue(java.time.LocalDate.parse(record[4]));
                selectGender(record[2]);
            } else {
                showAlert("Not Found", "No record found with the given ID.");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void selectGender(String gender) {
        for (Toggle toggle : genderGroup.getToggles()) {
            RadioButton button = (RadioButton) toggle;
            if (button.getText().equalsIgnoreCase(gender)) {
                button.setSelected(true);
                break;
            }
        }
    }

    private void closeApplication() {
        System.exit(0);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
