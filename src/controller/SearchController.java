package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.Album;
import model.Photo;
import model.Photos;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * This class contains the logic that controls the search screen
 *
 * @author Sreekar Vedula
 */
public class SearchController {
    /**
     * The container that displays the search results
     */
    public ListView<Photo> searchList;
    /**
     * The container that displays the image of the selected photo
     */
    public ImageView imageView;
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
     * Allows the user to pick a starting date for the search range
     */
    public DatePicker fromDate;
    /**
     * Allows the user to pick an ending date for the search range
     */
    public DatePicker toDate;
    /**
     * Field to input the name of the first tag
     */
    public TextField tagName1;
    /**
     * Field to input the value of the first tag
     */
    public TextField tagValue1;
    /**
     * Field to input the name of the second tag
     */
    public TextField tagName2;
    /**
     * Field to input the value of the second tag
     */
    public TextField tagValue2;
    /**
     * Dropdown menu to choose whether a conjunctive or disjunctive search is to be performed
     */
    public ChoiceBox<String> searchOptions;
    /**
     * Button to perform the search
     */
    public Button searchButton;
    /**
     * The set of all the search results
     */
    Set<Photo> searchResults = new HashSet<>();

    /**
     * Setup method that runs upon startup
     */
    public void initialize() {
        searchList.setPlaceholder(new Label("Search to see Results"));
        imageView.setImage(new Image("file:data/icon.png"));
        tagsLabel.setWrapText(true);
        searchOptions.getItems().addAll("AND", "OR");
        searchOptions.setValue("AND");
        searchList.setCellFactory(cell -> new ListCell<>() {
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
        searchList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            try {
                displayImage();
            } catch (Exception ignored) {
            }
        });
        Platform.runLater(() -> searchButton.requestFocus());
    }

    /**
     * Displays the selected image and its details in the viewing area
     */
    public void displayImage() {
        Photo photo = searchList.getSelectionModel().getSelectedItem();
        imageView.setImage(photo.getImage());
        captionLabel.setText(photo.getCaption());
        dateLabel.setText(photo.getDate().toString());
        tagsLabel.setText(photo.getTags().toString().substring(1, photo.getTags().toString().length() - 1));
    }

    /**
     * Performs the search based on the entered criteria and displays the results
     */
    public void search() {
        searchResults.clear();

        if (!fromDate.getEditor().getText().isEmpty() && !toDate.getEditor().getText().isEmpty()) {
            for (Album a : Photos.currentUser.getAlbums()) {
                for (Photo p : a.getPhotos()) {
                    LocalDate date = p.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    if (date.compareTo(fromDate.getValue()) >= 0 && date.compareTo(toDate.getValue()) <= 0) {
                        searchResults.add(p);
                    }
                }
            }
        } else if (!tagName1.getText().isEmpty() && !tagValue1.getText().isEmpty()) {
            String tag1 = tagName1.getText().toLowerCase() + " - " + tagValue1.getText().toLowerCase();

            if (tagName2.getText().isEmpty() ^ tagValue2.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Search Error");
                alert.setHeaderText("Invalid Input. Please Try Again");
                alert.showAndWait();
            } else if (!tagName2.getText().isEmpty() && !tagValue2.getText().isEmpty()) {
                String tag2 = tagName2.getText().toLowerCase() + " - " + tagValue2.getText().toLowerCase();
                for (Album a : Photos.currentUser.getAlbums()) {
                    for (Photo p : a.getPhotos()) {
                        if (searchOptions.getValue().equals("AND")) {
                            if (p.getTags().contains(tag1) && p.getTags().contains(tag2)) searchResults.add(p);
                        } else {
                            if (p.getTags().contains(tag1) || p.getTags().contains(tag2)) searchResults.add(p);
                        }
                    }
                }
            } else {
                for (Album a : Photos.currentUser.getAlbums()) {
                    for (Photo p : a.getPhotos()) {
                        if (p.getTags().contains(tag1)) searchResults.add(p);
                    }
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Search Error");
            alert.setHeaderText("Invalid Input. Please Try Again");
            alert.showAndWait();
        }
        searchList.setItems(FXCollections.observableList(new ArrayList<>(searchResults)));
        if (searchResults.isEmpty()) {
            searchList.setPlaceholder(new Label("No Search Results"));
            imageView.setImage(new Image("file:data/icon.png"));
            captionLabel.setText("");
            dateLabel.setText("");
            tagsLabel.setText("");
        }
        searchList.refresh();
        searchList.getSelectionModel().selectFirst();
        searchList.requestFocus();
    }

    /**
     * Clears the date value of the DatePickers
     */
    public void clearDate() {
        fromDate.setValue(null);
        toDate.setValue(null);
    }

    /**
     * Clears the tags that have been input so far
     */
    public void clearTags() {
        tagName1.clear();
        tagValue1.clear();
        tagName2.clear();
        tagValue2.clear();
    }

    /**
     * Returns to the album selection screen in the user panel
     *
     * @throws IOException An exception is thrown if the FXML file to the user panel cannot be loaded
     */
    public void goBack() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/User.fxml")));
        Stage stage = (Stage) imageView.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    /**
     * Creates a new album from the search results
     */
    public void createAlbum() {

        if (searchResults.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Search Results");
            alert.setHeaderText("No Photos Matched Your Search Criteria");
            alert.showAndWait();
        } else {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Create New Album");
            dialog.setHeaderText("Enter Album Name");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent() && !result.get().isEmpty()) {
                if (Photos.currentUser.getAlbums().contains(new Album(result.get()))) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Album Already Exists");
                    alert.setHeaderText("Please Enter another Album Name");
                    alert.showAndWait();
                } else {
                    Album newAlbum = new Album(result.get());
                    newAlbum.getPhotos().addAll(searchResults);
                    Photos.currentUser.getAlbums().add(newAlbum);
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
}
