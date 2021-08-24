package model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class represents a single registered user
 *
 * @author Sreekar Vedula
 */
public class User implements Serializable {
    /**
     * The username of the User as specified by the admin
     */
    private final String username;
    /**
     * The list of albums created by the user
     */
    ArrayList<Album> albums;

    /**
     * Creates a new User object with the specified username
     *
     * @param username The name of the user to be created
     */
    public User(String username) {
        this.username = username;
        albums = new ArrayList<>();
    }

    /**
     * Getter for the username field
     *
     * @return The username of this user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Getter for the albums field
     *
     * @return The list of albums created by this user
     */
    public ArrayList<Album> getAlbums() {
        return albums;
    }

    /**
     * Overridden method that provides the username of this user
     *
     * @return The username of this user
     */
    @Override
    public String toString() {
        return username;
    }

    /**
     * Overridden method that compares two users based on their username ignoring case in order to avoid duplicate users
     *
     * @param obj Another user object to be compared with this one
     * @return True if both users have the same username, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof User && this.username.equalsIgnoreCase(((User) obj).getUsername());
    }
}
