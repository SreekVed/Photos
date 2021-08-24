package model;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

/**
 * The Photos class is responsible for starting and closing the program, as well as saving and loading save data to the disk.
 *
 * @author Sreekar Vedula
 */
public class Photos extends Application {
    /**
     * The global list of registered users created by the admin
     */
    public static ArrayList<User> users = new ArrayList<>();
    /**
     * The user who is currently logged in
     */
    public static User currentUser = null;
    /**
     * The album that is currently open and being viewed
     */
    public static Album currentAlbum = null;

    /**
     * The main method launches the JavaFX application
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * The start method loads the serialized data from disk and then displays the main login screen
     *
     * @param stage The stage upon which the program runs
     * @throws Exception An exception is thrown in case there is no data to read or cannot be read correctly
     */
    @Override
    public void start(Stage stage) throws Exception {

        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data" + File.separator + "users.dat"));
            users = (ArrayList<User>) ois.readObject();
            ois.close();
        } catch (Exception ignored) {
        }

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/Login.fxml")));
        stage.setTitle("Photos");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.getIcons().add(new Image("file:data/icon.png"));
        stage.show();
    }

    /**
     * The stop method is called when the application is exited and serializes the session data to the disk
     *
     * @throws Exception An exception is thrown if the data cannot be stored to file for any reason
     */
    @Override
    public void stop() throws Exception {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data" + File.separator + "users.dat"));
        oos.writeObject(users);
        oos.close();
    }
}
