package model;

import javafx.scene.image.Image;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents an individual picture in an album
 *
 * @author Sreekar Vedula
 */
public class Photo implements Serializable {
    /**
     * The last modified date of this photo
     */
    Date date;
    /**
     * The caption of this photo
     */
    String caption = "";
    /**
     * A file object containing the path to this photo on the user's computer
     */
    File file;
    /**
     * The tags that have been added to this photo
     */
    Map<String, ArrayList<String>> tags;

    /**
     * The constructor that creates a new Photo object and assigns to it all of the necessary parameters
     *
     * @param file A file object containing the path to the photo
     */
    public Photo(File file) {
        this.file = file;
        date = new Date(file.lastModified());
        tags = new HashMap<>();
        tags.put("person", new ArrayList<>());
        tags.put("location", new ArrayList<>());
    }

    /**
     * Adds a new tag to this photo's map of tags
     *
     * @param name  The name of the tag
     * @param value The value of the tag
     */
    public void addTag(String name, String value) {
        tags.putIfAbsent(name, new ArrayList<>());
        tags.get(name).add(value);
    }

    /**
     * Getter for the caption field
     *
     * @return The caption of this photo
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Setter for the caption field
     *
     * @param caption The caption to be assigned to this photo
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * Gets all the tags of this photo by traversing all of the lists contained within the map of tags
     *
     * @return A list of strings containing all of the tags in name - value pairs
     */
    public ArrayList<String> getTags() {
        ArrayList<String> stringTags = new ArrayList<>();
        for (String key : this.tags.keySet()) {
            for (String value : this.tags.get(key)) {
                stringTags.add(key + " - " + value);
            }
        }
        return stringTags;
    }

    /**
     * Getter for the tags field
     *
     * @return The complete map of tags for this photo
     */
    public Map<String, ArrayList<String>> getTagMap() {
        return this.tags;
    }

    /**
     * Removes a tag from the map of tags for this photo
     *
     * @param name  The name of the tag to be removed
     * @param value The value of the tag to be removed
     */
    public void deleteTag(String name, String value) {
        this.tags.get(name).remove(value);
    }

    /**
     * Getter for the file field
     *
     * @return The file containing the filepath to this photo
     */
    public File getFile() {
        return file;
    }

    /**
     * Getter for the date field
     *
     * @return The last modified date of the photo file
     */
    public Date getDate() {
        return date;
    }

    /**
     * Creates an image object using the filepath of this photo
     *
     * @return The newly created image object to be displayed in an ImageView object
     */
    public Image getImage() {
        try {
            return new Image(this.getFile().toURI().toURL().toExternalForm());
        } catch (Exception e) {
            return new Image("file:data/icon.png");
        }
    }

    /**
     * An overridden method to display the caption in a ListView
     *
     * @return The caption of the photo with some padding to separate it from the thumbnail image
     */
    @Override
    public String toString() {
        return "\t" + this.caption;
    }

    /**
     * An overridden method that compares the filepaths of two photos to prevent adding the same photo twice to albums
     *
     * @param obj Another Photo object to be compared to this photo
     * @return True if the filepath is the same, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Photo && this.file.equals(((Photo) obj).getFile());
    }
}
