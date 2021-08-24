package controller;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Album;
import model.Photos;
import model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

/**
 * This class contains the logic that controls the user's screen which displays the album selection
 *
 * @author Sreekar Vedula
 */
public class UserController {
    /**
     * The text at the top of the screen displaying the user's name
     */
    public Label headerText;
    /**
     * The table that displays the details of each album
     */
    public TableView<Album> albumTable;
    /**
     * The column displaying the name of each album
     */
    public TableColumn<Album, String> nameColumn;
    /**
     * The column displaying the number of photos in each album
     */
    public TableColumn<Album, String> countColumn;
    /**
     * The column displaying the range of dates in each album
     */
    public TableColumn<Album, String> rangeColumn;
    /**
     * Button to create a new album
     */
    public Button createButton;
    /**
     * The current user logged on to the application
     */
    User currentUser = Photos.currentUser;
    /**
     * The list of albums of the current user
     */
    ArrayList<Album> albums = currentUser.getAlbums();

    /**
     * Setup method that runs upon startup
     */
    public void initialize() {
        headerText.setText(currentUser.getUsername() + "'s Album");
        albumTable.setItems(FXCollections.observableList(albums));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        countColumn.setCellValueFactory(p -> new ReadOnlyStringWrapper(String.valueOf(p.getValue().size())));
        rangeColumn.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().range()));
        albumTable.setPlaceholder(new Label("No Albums Found"));
        Platform.runLater(() -> createButton.requestFocus());
    }

    /**
     * Creates a new album and adds it to the list of albums for the current user
     */
    public void createAlbum() {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create New Album");
        dialog.setHeaderText("Enter Album Name");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().isEmpty()) {
            if (albums.contains(new Album(result.get()))) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Album Already Exists");
                alert.setHeaderText("Please Enter another Album Name");
                alert.showAndWait();
            } else {
                albums.add(new Album(result.get()));
                albumTable.setItems(FXCollections.observableList(albums));
                albumTable.refresh();
            }
        } else {
            if (result.isEmpty()) return;
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Album Name Blank");
            alert.setHeaderText("No Album Name Entered");
            alert.showAndWait();
        }

    }

    /**
     * Renames an existing album
     */
    public void renameAlbum() {

        if (albumTable.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Album Selected");
            alert.setHeaderText("Please Select an Album");
            alert.showAndWait();
        } else {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Rename Album");
            dialog.setHeaderText("Enter New Album Name");
            dialog.getEditor().setText(albumTable.getSelectionModel().getSelectedItem().getName());

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent() && !result.get().isEmpty()) {
                if (albums.contains(new Album(result.get()))) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Album Already Exists");
                    alert.setHeaderText("Please Enter another Album Name");
                    alert.showAndWait();
                } else {
                    albumTable.getSelectionModel().getSelectedItem().setName(result.get());
                    albumTable.setItems(FXCollections.observableList(albums));
                    albumTable.refresh();
                }
            } else {
                if (result.isEmpty()) return;
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Album Name Blank");
                alert.setHeaderText("No Album Name Entered");
                alert.showAndWait();
            }
        }

    }

    /**
     * Deletes an album from the user's list of albums
     */
    public void deleteAlbum() {
        if (albumTable.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Album Selected");
            alert.setHeaderText("Please Select an Album");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete");
            alert.setHeaderText("Are you sure you want to delete this album ?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                albums.remove(albumTable.getSelectionModel().getSelectedItem());
                albumTable.setItems(FXCollections.observableList(albums));
                albumTable.refresh();
            }
        }
    }

    /**
     * Opens an album and switches to the photo detail screen
     *
     * @throws IOException An exception is thrown if the FXML file to the photos screen cannot be loaded
     */
    public void openAlbum() throws IOException {

        if (albumTable.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Album Selected");
            alert.setHeaderText("Please Select an Album");
            alert.showAndWait();
        } else {
            Photos.currentAlbum = albumTable.getSelectionModel().getSelectedItem();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/Photos.fxml")));
            Stage stage = (Stage) albumTable.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
        }
    }

    /**
     * Switches to the photo search screen
     *
     * @throws IOException An exception is thrown if the FXML file to the search screen cannot be loaded
     */
    public void search() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/Search.fxml")));
        Stage stage = (Stage) albumTable.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    /**
     * Logs out the current user and switches back to the login screen
     *
     * @throws IOException An exception is thrown if the FXML file to the login screen cannot be loaded
     */
    public void logout() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/Login.fxml")));
        Stage stage = (Stage) albumTable.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        Photos.currentUser = null;
    }
}
