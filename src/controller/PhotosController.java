package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import model.Album;
import model.Photo;
import model.Photos;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

/**
 * This class contains the logic that controls the photo display screen shown upon opening an album
 *
 * @author Sreekar Vedula
 */
public class PhotosController {
    /**
     * The container that displays the list of photos in this album
     */
    public ListView<Photo> photoList;
    /**
     * The container that displays the selected photo
     */
    public ImageView imageView;
    /**
     * The text at the top of the screen displaying the name of the album
     */
    public Label headerText;
    /**
     * The label that displays the caption of the selected photo
     */
    public Label captionLabel;
    /**
     * The label that displays the tags of the selected photo
     */
    public Label tagsLabel;
    /**
     * The label that displays the date of the selected photo
     */
    public Label dateLabel;
    /**
     * The list of photos contained within the current selected album
     */
    ArrayList<Photo> photos = Photos.currentAlbum.getPhotos();

    /**
     * Setup method that runs upon startup
     */
    public void initialize() {
        photoList.setPlaceholder(new Label("No Photos Found"));
        headerText.setText("Photos in Album " + Photos.currentAlbum.getName());
        photoList.setItems(FXCollections.observableList(photos));
        imageView.setImage(new Image("file:data/icon.png"));
        tagsLabel.setWrapText(true);
        photoList.setCellFactory(cell -> new ListCell<>() {
            private final ImageView imgView = new ImageView();

            @Override
            protected void updateItem(Photo photo, boolean b) {
                super.updateItem(photo, b);
                if (b) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(photo.toString());
                    setFont(Font.font(14));
                    imgView.setImage(photo.getImage());
                    imgView.setFitHeight(30);
                    imgView.setPreserveRatio(true);
                    setGraphic(imgView);
                }
            }
        });
        photoList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            try {
                displayImage();
            } catch (Exception ignored) {
            }
        });
        photoList.getSelectionModel().selectFirst();
        Platform.runLater(() -> photoList.requestFocus());
    }

    /**
     * Updates the image and its details every time a new image is selected from the ListView
     */
    public void displayImage() {
        Photo photo = photoList.getSelectionModel().getSelectedItem();
        imageView.setImage(photo.getImage());
        captionLabel.setText(photo.getCaption());
        dateLabel.setText(photo.getDate().toString());
        tagsLabel.setText(photo.getTags().toString().substring(1, photo.getTags().toString().length() - 1));
    }

    /**
     * Adds a photo to the list of photos and displays it in the viewing area
     */
    public void addPhoto() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.bmp", "*.gif"));
        File photo = fileChooser.showOpenDialog(imageView.getScene().getWindow());
        if (photo != null) {
            photo = new File(new File(System.getProperty("user.dir")).toURI().relativize(photo.toURI()).getPath());
            if (photos.contains(new Photo(photo))) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Photo Already Exists");
                alert.setHeaderText("Please Choose another Photo");
                alert.showAndWait();
            } else {
                Photo duplicate = null;
                for (Album a : Photos.currentUser.getAlbums()) {
                    for (Photo p : a.getPhotos()) {
                        if (p.equals(new Photo(photo))) duplicate = p;
                    }
                }
                if (duplicate == null) photos.add(new Photo(photo));
                else photos.add(duplicate);
                photoList.setItems(FXCollections.observableList(photos));
                photoList.refresh();
                photoList.getSelectionModel().selectLast();
                photoList.requestFocus();
            }
        }

    }

    /**
     * Deletes a photo from the list of photos and displays a generic photo image
     */
    public void deletePhoto() {

        if (photoList.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Photo Selected");
            alert.setHeaderText("Please Select a Photo");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete");
            alert.setHeaderText("Are you sure you want to delete this photo ?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                photos.remove(photoList.getSelectionModel().getSelectedItem());
                photoList.setItems(FXCollections.observableList(photos));
                photoList.refresh();
                imageView.setImage(new Image("file:data/icon.png"));
                captionLabel.setText("");
                dateLabel.setText("");
                tagsLabel.setText("");
                photoList.requestFocus();
            }
        }

    }

    /**
     * Changes the caption of the selected photo
     */
    public void editCaption() {

        if (photoList.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Photo Selected");
            alert.setHeaderText("Please Select a Photo");
            alert.showAndWait();
        } else {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add/Edit Caption");
            dialog.setHeaderText("Enter Photo Caption");
            dialog.getEditor().setText(photoList.getSelectionModel().getSelectedItem().getCaption());
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(s -> photoList.getSelectionModel().getSelectedItem().setCaption(s));
            photoList.setItems(FXCollections.observableList(photos));
            photoList.refresh();
        }

    }

    /**
     * Adds a tag to the current photo
     */
    public void addTag() {

        if (photoList.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Photo Selected");
            alert.setHeaderText("Please Select a Photo");
            alert.showAndWait();
        } else {
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Add Tag");
            dialog.setHeaderText("Enter Tag Name and Value");
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);

            ComboBox<String> keys = new ComboBox<>(FXCollections.observableList(new ArrayList<>(photoList.getSelectionModel().getSelectedItem().getTagMap().keySet())));
            keys.setEditable(true);
            TextField values = new TextField();
            grid.add(new Label("Tag Name :"), 0, 0);
            grid.add(keys, 1, 0);
            grid.add(new Label("Tag Value :"), 0, 1);
            grid.add(values, 1, 1);

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> dialogButton == ButtonType.OK ? new Pair<>(keys.getSelectionModel().getSelectedItem(), values.getText()) : null);

            Optional<Pair<String, String>> result = dialog.showAndWait();

            if (result.isPresent()) {
                String key = result.get().getKey().toLowerCase();
                String value = result.get().getValue().toLowerCase();
                if (key.isEmpty() || value.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Tag");
                    alert.setHeaderText("Tag Name or Value is Blank");
                    alert.showAndWait();
                } else if ((photoList.getSelectionModel().getSelectedItem().getTagMap().containsKey(key) && photoList.getSelectionModel().getSelectedItem().getTagMap().get(key).contains(value)) || (key.equals("location") && !photoList.getSelectionModel().getSelectedItem().getTagMap().get("location").isEmpty())) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Duplicate Tag");
                    alert.setHeaderText("Tag Already Exists");
                    alert.showAndWait();
                } else {
                    photoList.getSelectionModel().getSelectedItem().addTag(key, value);
                    photoList.setItems(FXCollections.observableList(photos));
                    photoList.refresh();
                    photoList.requestFocus();
                }
            }
        }
    }

    /**
     * Deletes a tag from the current photo
     */
    public void deleteTag() {
        if (photoList.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Photo Selected");
            alert.setHeaderText("Please Select a Photo");
            alert.showAndWait();
        } else if (photoList.getSelectionModel().getSelectedItem().getTags().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Tags Added");
            alert.setHeaderText("This Photo does not have any tags");
            alert.showAndWait();
        } else {
            ChoiceDialog<String> dialog = new ChoiceDialog<>(photoList.getSelectionModel().getSelectedItem().getTags().get(0), photoList.getSelectionModel().getSelectedItem().getTags());

            dialog.setTitle("Delete Tag");
            dialog.setHeaderText("Choose the Tag to be deleted");
            dialog.setContentText("Tag :");

            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                String[] split = result.get().split(" - ");
                photoList.getSelectionModel().getSelectedItem().deleteTag(split[0], split[1]);
                photoList.setItems(FXCollections.observableList(photos));
                photoList.refresh();
                photoList.requestFocus();
            }
        }
    }

    /**
     * Copies the selected photo from the current album to another album
     */
    public void copyPhoto() {

        if (photoList.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Photo Selected");
            alert.setHeaderText("Please Select a Photo");
            alert.showAndWait();
        } else if (Photos.currentUser.getAlbums().size() == 1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Other Albums");
            alert.setHeaderText("Please Create another Album");
            alert.showAndWait();
        } else {
            ArrayList<Album> choices = new ArrayList<>(Photos.currentUser.getAlbums());
            choices.remove(Photos.currentAlbum);
            ChoiceDialog<Album> dialog = new ChoiceDialog<>(choices.get(0), choices);

            dialog.setTitle("Copy Photo");
            dialog.setHeaderText("Choose Destination Album");
            dialog.setContentText("Album :");

            Optional<Album> result = dialog.showAndWait();

            if (result.isPresent()) {

                if (result.get().getPhotos().contains(photoList.getSelectionModel().getSelectedItem())) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Duplicate Photo");
                    alert.setHeaderText("Photo already exists in Album " + result.get().getName());
                    alert.showAndWait();
                } else {
                    result.get().getPhotos().add(photoList.getSelectionModel().getSelectedItem());
                }

            }
        }
    }

    /**
     * Moves the selected photo to another album
     */
    public void movePhoto() {

        if (photoList.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Photo Selected");
            alert.setHeaderText("Please Select a Photo");
            alert.showAndWait();
        } else if (Photos.currentUser.getAlbums().size() == 1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Other Albums");
            alert.setHeaderText("Please Create another Album");
            alert.showAndWait();
        } else {
            ArrayList<Album> choices = new ArrayList<>(Photos.currentUser.getAlbums());
            choices.remove(Photos.currentAlbum);
            ChoiceDialog<Album> dialog = new ChoiceDialog<>(choices.get(0), choices);

            dialog.setTitle("Move Photo");
            dialog.setHeaderText("Choose Destination Album");
            dialog.setContentText("Album :");

            Optional<Album> result = dialog.showAndWait();

            if (result.isPresent()) {

                if (result.get().getPhotos().contains(photoList.getSelectionModel().getSelectedItem())) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Duplicate Photo");
                    alert.setHeaderText("Photo already exists in Album " + result.get().getName());
                    alert.showAndWait();
                } else {
                    result.get().getPhotos().add(photoList.getSelectionModel().getSelectedItem());
                    photos.remove(photoList.getSelectionModel().getSelectedItem());
                    photoList.setItems(FXCollections.observableList(photos));
                    photoList.refresh();
                    imageView.setImage(new Image("file:data/icon.png"));
                    captionLabel.setText("");
                    dateLabel.setText("");
                    tagsLabel.setText("");
                    photoList.requestFocus();
                }
            }
        }
    }

    /**
     * Displays the previous image in the ListView and its details
     */
    public void prevPhoto() {
        photoList.getSelectionModel().selectPrevious();
        photoList.requestFocus();
    }

    /**
     * Displays the previous image in the ListView and its details
     */
    public void nextPhoto() {
        photoList.getSelectionModel().selectNext();
        photoList.requestFocus();
    }

    /**
     * Returns to the previous screen which displays the albums of the user
     *
     * @throws IOException An exception is thrown if the FXML file for the user panel cannot be loaded
     */
    public void goBack() throws IOException {
        Photos.currentAlbum = null;
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/User.fxml")));
        Stage stage = (Stage) imageView.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

}
