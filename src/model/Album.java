package model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * This class represents an album created by a user
 *
 * @author Sreekar Vedula
 */
public class Album implements Serializable {
    /**
     * The list of photos contained within this album
     */
    ArrayList<Photo> photos;
    /**
     * The name of the album as specified by the user
     */
    private String name;

    /**
     * Constructor to create a new album
     *
     * @param name The name of the new album
     */
    public Album(String name) {
        this.photos = new ArrayList<>();
        this.name = name;
    }

    /**
     * Getter for the photos field
     *
     * @return The list of photos contained within this album
     */
    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    /**
     * The size of this album
     *
     * @return The number of photos in this album
     */
    public int size() {
        return photos.size();
    }

    /**
     * Getter for the name field
     *
     * @return The name of this album
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the name field
     *
     * @param name The new name to rename this album
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Overridden method to check if two albums have the same name in order to check for and avoid duplicate names
     *
     * @param obj Another album with which this album is to be compared
     * @return True if the names of the albums are equal, false if otherwise
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Album && this.name.equalsIgnoreCase(((Album) obj).getName());
    }

    /**
     * Overridden method to get the Album's name for display in the TableView
     *
     * @return The name of this album
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Calculates the range of dates of the photos in this album using their last modified dates
     *
     * @return A string containing the first and last modified dates of the photos in the album for display in the TableView
     */
    public String range() {
        if (photos.size() == 0) return "";
        photos.sort(Comparator.comparing(Photo::getDate));
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        return sdf.format(photos.get(0).getDate()) + " - " + sdf.format(photos.get(photos.size() - 1).getDate());
    }
}
