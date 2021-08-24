package controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Photos;
import model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * This class contains the logic that controls the login screen
 *
 * @author Sreekar Vedula
 */
public class LoginController {
    /**
     * The field where the username is entered in order to login
     */
    public TextField userField;
    /**
     * The button used to login
     */
    public Button loginButton;
    /**
     * The list of registered users
     */
    ArrayList<User> users = Photos.users;

    /**
     * Setup method that runs upon startup
     */
    public void initialize() {
        Platform.runLater(() -> loginButton.requestFocus());
    }

    /**
     * The method used to login to either the admin panel or the user panel
     *
     * @throws IOException An exception is thrown if the needed FXML file cannot be loaded
     */
    public void login() throws IOException {
        String username = userField.getText().toLowerCase();
        if (username.equals("admin")) {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/Admin.fxml")));
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } else if (username.isEmpty() || !users.contains(new User(userField.getText()))) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Please Enter a Valid Username");
            alert.setTitle("Invalid Username");
            alert.show();
            userField.clear();
            userField.requestFocus();
        } else {
            Photos.currentUser = users.get(users.indexOf(new User(userField.getText())));
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/User.fxml")));
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
        }
    }

}
