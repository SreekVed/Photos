package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Photos;
import model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

/**
 * This class contains the logic that controls the admin screen
 *
 * @author Sreekar Vedula
 */
public class AdminController {
    /**
     * Button to add a user
     */
    public Button addButton;
    /**
     * List of registered users
     */
    public ArrayList<User> users = Photos.users;
    /**
     * Container that displays the list of registered users
     */
    public ListView<User> userList = new ListView<>();

    /**
     * Setup method that runs upon startup
     */
    public void initialize() {
        userList.setItems(FXCollections.observableList(users));
        Platform.runLater(() -> addButton.requestFocus());
        userList.setPlaceholder(new Label("No Users Found"));
    }

    /**
     * Registers a new user on the system and adds them to the list of users
     */
    public void addUser() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create New User");
        dialog.setHeaderText("Enter Username");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().isEmpty()) {
            if (result.get().equals("admin") || users.contains(new User(result.get()))) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("User Already Exists");
                alert.setHeaderText("Please Enter a Different Username");
                alert.showAndWait();
            } else {
                users.add(new User(result.get()));
                userList.setItems(FXCollections.observableList(users));
            }
        } else {
            if (result.isEmpty()) return;
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Username Blank");
            alert.setHeaderText("No Username Entered");
            alert.showAndWait();
        }
    }

    /**
     * Logs out of the admin panel and goes back to the login screen
     *
     * @throws IOException Throws an exception if the FXML file for the login screen cannot be loaded
     */
    public void logout() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/Login.fxml")));
        Stage stage = (Stage) addButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        Photos.currentUser = null;
    }

    /**
     * Deletes a user from the list of registered users
     */
    public void deleteUser() {

        if (userList.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No User Selected");
            alert.setHeaderText("Please Select a User to be Deleted");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete");
            alert.setHeaderText("Are you sure you want to delete this user ?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                users.remove(userList.getSelectionModel().getSelectedItem());
                userList.setItems(FXCollections.observableList(users));
            }
        }
    }
}